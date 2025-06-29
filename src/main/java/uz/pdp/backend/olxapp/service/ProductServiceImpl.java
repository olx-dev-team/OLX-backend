package uz.pdp.backend.olxapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.pdp.backend.olxapp.entity.*;
import uz.pdp.backend.olxapp.entity.abstractEntity.AbstractEntity;
import uz.pdp.backend.olxapp.entity.abstractEntity.LongIdAbstract;
import uz.pdp.backend.olxapp.enums.Role;
import uz.pdp.backend.olxapp.enums.Status;
import uz.pdp.backend.olxapp.exception.EntityNotFoundException;
import uz.pdp.backend.olxapp.exception.IllegalActionException;
import uz.pdp.backend.olxapp.mapper.ProductMapper;
import uz.pdp.backend.olxapp.payload.*;
import uz.pdp.backend.olxapp.repository.AttachmentRepository;
import uz.pdp.backend.olxapp.repository.CategoryRepository;
import uz.pdp.backend.olxapp.repository.ProductRepository;
import uz.pdp.backend.olxapp.specifation.ProductSpecification;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final AttachmentService attachmentService;
    private final CategoryRepository categoryRepository;
    private final AttachmentRepository attachmentRepository;

    @Override
    public PageDTO<ProductDTO> read(Integer page, Integer size) {
        Sort sort = Sort.by(LongIdAbstract.Fields.id);
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        //Status ACTIVE bo'lganlarni olib kelamiz
        Page<Product> productPage = productRepository.findAllByStatus(pageRequest, Status.ACTIVE);

        return new PageDTO<>(
                productPage.getContent().stream().map(productMapper::toDto).toList(),
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.isLast(),
                productPage.isFirst(),
                productPage.getNumberOfElements(),
                productPage.isEmpty()
        );
    }

    @Override
    public ProductDTO read(Long id) {

        //Bitta productni olib kelamiz uni ham statusi ACTIVE bo'lishi kerak
        Product product = productRepository.findByIdAndStatus(id, List.of(Status.ACTIVE))
                .orElseThrow(() -> new EntityNotFoundException("Your product may be is not active or not found", HttpStatus.NOT_FOUND));

        return productMapper.toDto(product);
    }

    @Override
    public ProductDTO increaseViewCount(Long id) {
        Product product = productRepository
                .findByIdAndStatus(id, List.of(Status.ACTIVE))
                .orElseThrow(() -> new EntityNotFoundException("Your product may be is not active or not found", HttpStatus.NOT_FOUND));

        product.setViewCounter(product.getViewCounter() + 1);
        return productMapper.toDto(productRepository.save(product));
    }

// ProductServiceImpl.java

    @Override
    @Transactional
    public ProductDTO save(ProductReqDTO productReqDTO) {


        Category category = categoryRepository.findByIdOrThrow(productReqDTO.getCategoryId());

        //noinspection ExtractMethodRecommender
        if (!category.getChildren().isEmpty()) {
            throw new IllegalArgumentException("Category has child categories");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof User user)) {
            throw new AccessDeniedException("User is not authenticated");
        }

        // 1. Product entitini yaratish
        Product product = new Product();
        product.setTitle(productReqDTO.getTitle());
        product.setDescription(productReqDTO.getDescription());
        product.setPrice(productReqDTO.getPrice());
        product.setCategory(category); // yoki categoryService.getById(id)
///        product.setActive(productReqDTO.isActive()); buni orniga endi biz Enum bilan ishlayapmiz
        product.setStatus(Status.PENDING_REVIEW);
        product.setCreatedBy(user);

        Product savedProduct = productRepository.save(product);// asosiy product saqlanadi

        // 2. Rasm fayllarini Attachment qilib saqlash
        List<ProductNewImageDTO> imageDTOS = productReqDTO.getImageDTOS();

        if (imageDTOS.size() > 8) {
            throw new IllegalActionException("Maximum 8 images are allowed.", HttpStatus.BAD_REQUEST);
        }

        int count = (int) imageDTOS.stream()
                .filter(dto -> dto.getMain() != null && dto.getMain())
                .count();

        if (count > 1) {
            throw new IllegalActionException("One main image should be selected", HttpStatus.BAD_REQUEST);
        }


        if (!imageDTOS.isEmpty()) {
            for (ProductNewImageDTO imageDTO : imageDTOS) {
                MultipartFile file = imageDTO.getFile();
                boolean isMain = Boolean.TRUE.equals(imageDTO.getMain());

                // ❗ attachment null bo'lishi mumkin, tekshirib olamiz
                if (file == null || file.isEmpty()) {
                    continue; // rasm yuborilmagan bo‘lsa, tashlab ketamiz
                }

                // Attachment saqlaymiz
                AttachmentDTO attachmentDTO = attachmentService.upload(file); // bu DTO ichida ID bo'lishi kerak
                Attachment attachment = attachmentRepository.findByIdOrElseTrow(attachmentDTO.getId());

                // ProductImage ni yaratamiz
                ProductImage image = new ProductImage();
                image.setProduct(product);
                image.setAttachment(attachment);
                image.setMain(isMain);

                savedProduct.getProductImages().add(image);
            }
        }


        // 3. ProductDTO qaytarish
        return productMapper.toDto(product);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductDTO updateProduct(Long productId, ProductUpdateDTO productUpdateDTO) {

        Product product = productRepository
                .findByIdAndStatus(productId, List.of(Status.ACTIVE, Status.REJECTED))
                .orElseThrow(() -> new EntityNotFoundException("Your product may be is not active or not found", HttpStatus.NOT_FOUND));

        List<ExistedImageDTO> existedImages = Optional.ofNullable(productUpdateDTO.getExistedImages()).orElse(Collections.emptyList());
        List<ProductNewImageDTO> newImages = Optional.ofNullable(productUpdateDTO.getProductNewImages()).orElse(Collections.emptyList());

        if (existedImages.size() + newImages.size() > 8) {
            throw new IllegalActionException("Maximum 8 images are allowed.", HttpStatus.BAD_REQUEST);
        }

        long isMainCountInExistingImages = productUpdateDTO.getExistedImages().stream()
                .filter(ExistedImageDTO::isMain)
                .count();

        long isMainCountInNewImages = productUpdateDTO.getProductNewImages().stream()
                .filter(ProductNewImageDTO::getMain)
                .count();

        if (isMainCountInExistingImages + isMainCountInNewImages != 1) {
            throw new IllegalActionException("One main image should be selected", HttpStatus.BAD_REQUEST);
        }

        product.getProductImages().forEach(image -> image.setMain(Boolean.FALSE));

        if (!newImages.isEmpty()) {
            for (ProductNewImageDTO productNewImage : productUpdateDTO.getProductNewImages()) {

                MultipartFile file = productNewImage.getFile();
                AttachmentDTO upload = attachmentService.upload(file);
                Attachment attachment = attachmentRepository.findByIdOrElseTrow(upload.getId());

                if (Boolean.TRUE.equals(productNewImage.getMain())) {
                    product.getProductImages().forEach(image -> image.setMain(Boolean.FALSE));
                    product.getProductImages().add(new ProductImage(product, attachment, Boolean.TRUE));
                } else {
                    product.getProductImages().add(new ProductImage(product, attachment, Boolean.FALSE));
                }


            }

        }

        Set<Long> idsToKeep = productUpdateDTO.getExistedImages().stream()
                .map(ExistedImageDTO::getImageId)
                .collect(Collectors.toSet());

        Map<Long, Boolean> idToMainMap = productUpdateDTO.getExistedImages().stream()
                .collect(Collectors.toMap(ExistedImageDTO::getImageId, ExistedImageDTO::isMain));

        product.getProductImages().removeIf(image -> !idsToKeep.contains(image.getId()));

        product.getProductImages().forEach(image -> {
            image.setMain(Boolean.FALSE); // default to false
            if (idToMainMap.getOrDefault(image.getId(), false)) {
                image.setMain(Boolean.TRUE);
            }
        });

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof User user)) {
            throw new AccessDeniedException("User is not authenticated");
        }
        if (!product.getCreatedBy().getId().equals(user.getId()) || user.getRole().equals(Role.ADMIN)) {
            throw new AccessDeniedException("You are not the owner of this product");
        }

        product.setTitle(productUpdateDTO.getTitle());
        product.setDescription(productUpdateDTO.getDescription());
        product.setPrice(productUpdateDTO.getPrice());
        product.setStatus(Status.PENDING_REVIEW);


        Product updatedProduct = productRepository.save(product);

        return productMapper.toDto(updatedProduct);
    }


    @Override
    @Transactional
    public void updateStatus(Long id) {

        Product product = productRepository
                .findByIdAndStatus(id, List.of(Status.ACTIVE))
                .orElseThrow(() -> new EntityNotFoundException("Product not found", HttpStatus.NOT_FOUND));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof User user) {
            if (!user.getId().equals(product.getCreatedBy().getId())) {
                throw new AccessDeniedException("You are not the owner of this product");
            }
        } else {
            throw new AccessDeniedException("User is not authenticated");
        }


        product.setStatus(Status.PENDING_REVIEW);

        productRepository.save(product);
    }

    @Override
    public void deleteProduct(Long id) {

        Product product = productRepository
                .findByIdAndStatus(id, List.of(Status.ACTIVE))
                .orElseThrow(() -> new EntityNotFoundException("Product not found", HttpStatus.NOT_FOUND));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof User user)) {
            throw new AccessDeniedException("User is not authenticated");
        }
        if (!user.getId().equals(product.getCreatedBy().getId())) {
            throw new AccessDeniedException("You are not the owner of this product");
        }

        if (product.getStatus().equals(Status.DRAFT)) {
            throw new IllegalActionException("This product is in draft state and cannot be deleted", HttpStatus.BAD_REQUEST);
        }

        productRepository.delete(product);

    }


    @Override
    public PageDTO<ProductDTO> getUserProductsIsApprovedTrue(Integer page, Integer size) {

        Sort sort = Sort.by(LongIdAbstract.Fields.id);
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof User user)) {
            throw new AccessDeniedException("User is not authenticated");
        }

        Page<Product> byCreatedBy = productRepository.findByCreatedByAndStatus(user, Status.ACTIVE, pageRequest);

        return new PageDTO<>(
                byCreatedBy.getContent().stream().map(productMapper::toDto).toList(),
                byCreatedBy.getNumber(),
                byCreatedBy.getSize(),
                byCreatedBy.getTotalElements(),
                byCreatedBy.getTotalPages(),
                byCreatedBy.isLast(),
                byCreatedBy.isFirst(),
                byCreatedBy.getNumberOfElements(),
                byCreatedBy.isEmpty()
        );
    }

    @Override
    public PageDTO<ProductDTO> getWaitingProducts(Integer page, Integer size) {

        Sort sort = Sort.by(AbstractEntity.Fields.updatedAt);
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof User user)) {
            throw new AccessDeniedException("User is not authenticated");
        }

        Page<Product> byCreatedBy = productRepository.findByCreatedByAndStatus(user, Status.PENDING_REVIEW, pageRequest);

        return new PageDTO<>(
                byCreatedBy.getContent().stream().map(productMapper::toDto).toList(),
                byCreatedBy.getNumber(),
                byCreatedBy.getSize(),
                byCreatedBy.getTotalElements(),
                byCreatedBy.getTotalPages(),
                byCreatedBy.isLast(),
                byCreatedBy.isFirst(),
                byCreatedBy.getNumberOfElements(),
                byCreatedBy.isEmpty()
        );

    }

    @Override
    public PageDTO<ProductDTO> getInactiveProducts(Integer page, Integer size) {

        Sort sort = Sort.by(AbstractEntity.Fields.updatedAt);
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof User user)) {
            throw new AccessDeniedException("User is not authenticated");
        }

        Page<Product> byCreatedBy = productRepository.findByCreatedByAndStatus(user, Status.INACTIVE, pageRequest);

        return new PageDTO<>(
                byCreatedBy.getContent().stream().map(productMapper::toDto).toList(),
                byCreatedBy.getNumber(),
                byCreatedBy.getSize(),
                byCreatedBy.getTotalElements(),
                byCreatedBy.getTotalPages(),
                byCreatedBy.isLast(),
                byCreatedBy.isFirst(),
                byCreatedBy.getNumberOfElements(),
                byCreatedBy.isEmpty()
        );

    }

    @Override
    public PageDTO<ProductDTO> getRejectedProducts(Integer page, Integer size) {

        Sort sort = Sort.by(AbstractEntity.Fields.updatedAt);
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof User user)) {
            throw new AccessDeniedException("User is not authenticated");
        }

        Page<Product> rejectedProducts = productRepository.findByCreatedByAndStatus(
                user,
                Status.REJECTED,
                pageRequest
        );

        return new PageDTO<>(
                rejectedProducts.getContent().stream().map(productMapper::toDto).toList(),
                rejectedProducts.getNumber(),
                rejectedProducts.getSize(),
                rejectedProducts.getTotalElements(),
                rejectedProducts.getTotalPages(),
                rejectedProducts.isLast(),
                rejectedProducts.isFirst(),
                rejectedProducts.getNumberOfElements(),
                rejectedProducts.isEmpty()
        );

    }

    public PageDTO<ProductDTO> searchProducts(ProductFilterDTO filterDTO, Pageable pageable) {

        Specification<Product> specification = ProductSpecification.filterBy(filterDTO);

        Page<Product> all = productRepository.findAll(specification, pageable);

        return new PageDTO<>(all.getContent().stream().map(productMapper::toDto).toList(),
                all.getNumber(),
                all.getSize(),
                all.getTotalElements(),
                all.getTotalPages(),
                all.isLast(),
                all.isFirst(),
                all.getNumberOfElements(),
                all.isEmpty()
        );

    }


}