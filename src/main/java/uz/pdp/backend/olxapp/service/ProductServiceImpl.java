package uz.pdp.backend.olxapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import uz.pdp.backend.olxapp.mapper.utils.ProductUtilsMapper;
import uz.pdp.backend.olxapp.payload.*;
import uz.pdp.backend.olxapp.repository.AttachmentRepository;
import uz.pdp.backend.olxapp.repository.CategoryRepository;
import uz.pdp.backend.olxapp.repository.ProductRepository;
import uz.pdp.backend.olxapp.specifation.ProductSpecification;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final AttachmentService attachmentService;
    private final CategoryRepository categoryRepository;
    private final AttachmentRepository attachmentRepository;
    private final ProductUtilsMapper productUtilsMapper;

    @Override
    public PageDTO<ProductDTO> read(Integer page, Integer size) {
        log.info("Fetching products - page: {}, size: {}", page, size);

        Sort sort = Sort.by(LongIdAbstract.Fields.id);
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        Page<Product> productPage = productRepository.findAllByStatus(pageRequest, Status.ACTIVE);

        if (productPage.isEmpty()) {
            log.warn("No active products found on page {} with size {}", page, size);
        } else {
            log.info("Found {} products on page {} out of {} total pages",
                    productPage.getNumberOfElements(), productPage.getNumber(), productPage.getTotalPages());
        }

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
        log.info("Reading product with ID: {}", id);

        Product product = productRepository.findByIdAndStatus(id, List.of(Status.ACTIVE))
                .orElseThrow(() -> {
                    log.warn("Product with ID {} not found or not active", id);
                    return new EntityNotFoundException("Your product may be is not active or not found", HttpStatus.NOT_FOUND);
                });

        log.info("Successfully fetched product with ID: {}", id);
        return productMapper.toDto(product);
    }

    @Override
    public ProductDTO increaseViewCount(Long id) {
        log.info("Attempting to increase view count for product with ID: {}", id);

        Product product = productRepository
                .findByIdAndStatus(id, List.of(Status.ACTIVE))
                .orElseThrow(() -> {
                    log.warn("Product with ID {} not found or not active", id);
                    return new EntityNotFoundException("Your product may be is not active or not found", HttpStatus.NOT_FOUND);
                });

        int oldCount = product.getViewCounter();
        product.setViewCounter(oldCount + 1);
        Product savedProduct = productRepository.save(product);

        log.info("View count for product ID {} increased from {} to {}", id, oldCount, savedProduct.getViewCounter());

        return productMapper.toDto(savedProduct);
    }

// ProductServiceImpl.java

    /// teskshirildi hammsi save qilyapti
    @Override
    @Transactional
    public ProductDTO save(ProductReqDTO productReqDTO) {
        log.info("Attempting to save new product: {}", productReqDTO.getTitle());

        Category category = categoryRepository.findByIdOrThrow(productReqDTO.getCategoryId());
        if (!category.getChildren().isEmpty()) {
            log.warn("Attempted to assign product to non-leaf category: {}", category.getName());
            throw new IllegalArgumentException("Category has child categories");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof User user)) {
            log.error("Unauthenticated user attempted to save product");
            throw new AccessDeniedException("User is not authenticated");
        }

        Product product = new Product();
        product.setTitle(productReqDTO.getTitle());
        product.setDescription(productReqDTO.getDescription());
        product.setPrice(productReqDTO.getPrice());
        product.setCategory(category);
        product.setStatus(Status.PENDING_REVIEW);
        product.setCreatedBy(user);

        Product savedProduct = productRepository.save(product);
        log.debug("Saved product with ID: {}", savedProduct.getId());

        // Handle images
        List<ProductNewImageDTO> imageDTOS = productReqDTO.getImageDTOS();
        if (imageDTOS.size() > 8) {
            log.warn("User {} tried to upload more than 8 images", user.getId());
            throw new IllegalActionException("Maximum 8 images are allowed.", HttpStatus.BAD_REQUEST);
        }

        int count = (int) imageDTOS.stream().filter(ProductNewImageDTO::isMain).count();
        if (count != 1) {
            log.warn("Incorrect number of main images ({}) provided by user {}", count, user.getId());
            throw new IllegalActionException("One main image should be selected", HttpStatus.BAD_REQUEST);
        }

        for (ProductNewImageDTO imageDTO : imageDTOS) {
            MultipartFile file = imageDTO.getFile();
            boolean isMain = imageDTO.isMain();
            if (file == null || file.isEmpty()) continue;

            AttachmentDTO attachmentDTO = attachmentService.upload(file);
            Attachment attachment = attachmentRepository.findByIdOrElseTrow(attachmentDTO.getId());

            ProductImage image = new ProductImage();
            image.setProduct(product);
            image.setAttachment(attachment);
            image.setMain(isMain);

            savedProduct.getProductImages().add(image);
        }

        log.info("Product created successfully with ID: {}", savedProduct.getId());
        return productMapper.toDto(savedProduct);
    }

    ///  tekshirilgi
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductDTO updateProduct(Long productId, ProductUpdateDTO productUpdateDTO) {

        log.info("Starting update for product ID: {}", productId);

        // 1. Mahsulotni topish va tekshirish
        Product product = productRepository
                .findByIdAndStatus(productId, List.of(Status.ACTIVE, Status.REJECTED, Status.PENDING_REVIEW))
                .orElseThrow(() -> {
                    log.warn("Product ID {} not found or not editable", productId);
                    return new EntityNotFoundException("Product not found or not in an editable state", HttpStatus.NOT_FOUND);
                });

        // Foydalanuvchi huquqini tekshirish (rasmlarni qayta ishlashdan oldin)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof User user)) {
            log.warn("Unauthenticated user tried to update product ID {}", productId);
            throw new AccessDeniedException("User is not authenticated");
        }

        if (!product.getCreatedBy().getId().equals(user.getId()) && !user.getRole().equals(Role.ADMIN)) {
            log.warn("User {} attempted unauthorized update of product ID {}", user.getId(), productId);
            throw new AccessDeniedException("You are not the owner of this product");
        }

        // 2. Kiruvchi ma'lumotlarni xavfsiz olish
        List<ExistedImageDTO> existedImages = Optional.ofNullable(productUpdateDTO.getExistedImages()).orElse(Collections.emptyList());
        List<ProductNewImageDTO> newImages = Optional.ofNullable(productUpdateDTO.getProductNewImages()).orElse(Collections.emptyList());

        log.debug("Existed images: {}, New images: {}", existedImages.size(), newImages.size());

        // 3. Rasm soni va asosiy rasm validatsiyasi
        if (existedImages.size() + newImages.size() > 8) {
            log.warn("Product update failed: too many images ({} total)", existedImages.size() + newImages.size());
            throw new IllegalActionException("Maximum 8 images are allowed.", HttpStatus.BAD_REQUEST);
        }

        // 'boolean' uchun 'isMain()' dan foydalanamiz
        long mainImageCount = existedImages.stream().filter(ExistedImageDTO::isMain).count() +
                newImages.stream().filter(ProductNewImageDTO::isMain).count();

        if (mainImageCount != 1) {
            log.warn("Product update failed: {} main images provided", mainImageCount);
            throw new IllegalActionException("Exactly one main image must be selected.", HttpStatus.BAD_REQUEST);
        }

        // 4. Rasmlarni qayta ishlash (TO'G'RI KETMA-KETLIK)

        // A. O'chirilishi kerak bo'lgan rasmlarni olib tashlash
        Set<Long> existedImageIdsToKeep = existedImages.stream()
                .map(ExistedImageDTO::getImageId)
                .collect(Collectors.toSet());

        // orphanRemoval=true bilan ishlashi kerak
        product.getProductImages().removeIf(image -> !existedImageIdsToKeep.contains(image.getId()));

        // B. Mavjud qolgan rasmlarning 'main' statusini yangilash
        Map<Long, Boolean> idToMainMap = existedImages.stream()
                .collect(Collectors.toMap(ExistedImageDTO::getImageId, ExistedImageDTO::isMain));

        product.getProductImages().forEach(image -> {
            image.setMain(idToMainMap.getOrDefault(image.getId(), false));
        });

        // C. Yangi rasmlarni qo'shish
        if (!newImages.isEmpty()) {
            for (ProductNewImageDTO newImageDto : newImages) {
                if (newImageDto.getFile() == null || newImageDto.getFile().isEmpty()) {
                    continue;
                }

                AttachmentDTO upload = attachmentService.upload(newImageDto.getFile());
                Attachment attachment = attachmentRepository.findByIdOrElseTrow(upload.getId());

                // 'isMain()' metodidan foydalanamiz
                product.getProductImages().add(
                        new ProductImage(product, attachment, newImageDto.isMain())
                );
            }
        }

        // 5. Mahsulotning boshqa maydonlarini yangilash
        product.setTitle(productUpdateDTO.getTitle());
        product.setDescription(productUpdateDTO.getDescription());
        product.setPrice(productUpdateDTO.getPrice());
        product.setStatus(Status.PENDING_REVIEW);

        // 6. O'zgarishlarni saqlash
        Product updatedProduct = productRepository.save(product);

        log.info("Successfully updated product ID: {}", productId);

        return productMapper.toDto(updatedProduct);
    }

    @Override
    @Transactional
    public void updateStatus(Long id) {
        log.info("Attempting to update status for product ID: {}", id);

        Product product = productRepository
                .findByIdAndStatus(id, List.of(Status.ACTIVE))
                .orElseThrow(() -> {
                    log.warn("Product with ID {} not found or not active", id);
                    return new EntityNotFoundException("Product not found", HttpStatus.NOT_FOUND);
                });

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof User user) {
            if (!user.getId().equals(product.getCreatedBy().getId())) {
                log.warn("User ID {} attempted to update status of product ID {} not owned by them", user.getId(), id);
                throw new AccessDeniedException("You are not the owner of this product");
            }
        } else {
            log.warn("Unauthenticated access attempt to update status of product ID {}", id);
            throw new AccessDeniedException("User is not authenticated");
        }

        product.setStatus(Status.PENDING_REVIEW);
        productRepository.save(product);

        log.info("Successfully updated product ID {} status to PENDING_REVIEW", id);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        log.info("Attempting to delete product with ID: {}", id);

        Product product = productRepository
                .findByIdAndStatus(id, List.of(Status.ACTIVE))
                .orElseThrow(() -> {
                    log.warn("Product with ID {} not found or not active", id);
                    return new EntityNotFoundException("Product not found", HttpStatus.NOT_FOUND);
                });

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof User user)) {
            log.warn("Unauthenticated attempt to delete product ID {}", id);
            throw new AccessDeniedException("User is not authenticated");
        }

        if (!user.getId().equals(product.getCreatedBy().getId())) {
            log.warn("User ID {} attempted to delete product ID {} not owned by them", user.getId(), id);
            throw new AccessDeniedException("You are not the owner of this product");
        }

        if (product.getStatus().equals(Status.DRAFT)) {
            log.warn("Attempt to delete product ID {} in draft state", id);
            throw new IllegalActionException("This product is in draft state and cannot be deleted", HttpStatus.BAD_REQUEST);
        }

        productRepository.delete(product);
        log.info("Product with ID {} deleted successfully by user ID {}", id, user.getId());
    }


    /// tekshirildi userning ozining productlarni aktiv bolib turganlari ni olib kelmoqda
    @Override
    public PageDTO<ProductDTO> getMyProductsIsActive(Integer page, Integer size) {

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

    /// tekshirildi moderatsiyadan otishi kutilayotgan productlarni olib kelmoqda
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

    /// tekshirildi userning ozining productlarni inactive qilganlarini olib kelmoqda
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

    /// tekshirildi userning ozining productlarni rejected qilganlarini olib kelmoqda
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

    /// tekshirildi ishlayapti
    @Override
    public PageDTO<ProductDTO> searchProducts(ProductFilterDTO filterDTO, Integer page, Integer size) {
        log.info("Searching products with filters: {}, page: {}, size: {}", filterDTO, page, size);

        Specification<Product> specification = ProductSpecification.filterBy(filterDTO);

        Sort sort = Sort.by(LongIdAbstract.Fields.id);
        PageRequest pageable = PageRequest.of(page, size, sort);
        Page<Product> all = productRepository.findAll(specification, pageable);

        log.info("Found {} products matching the filters", all.getTotalElements());

        return new PageDTO<>(
                all.getContent().stream().map(productMapper::toDto).toList(),
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


    public ProductModerationStatusDTO getModerationStatus(Long productId, User currentUser) {
        // 1. Находим продукт в базе. Используем запрос, который сразу подтянет причины, чтобы избежать N+1.
        Product product = productRepository.findByIdWithRejectionReasons(productId) // Нам нужно создать этот метод в репозитории
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId, HttpStatus.NOT_FOUND));

        // 2. Проверяем права доступа. Это КРИТИЧЕСКИ ВАЖНО.
        if (!Objects.equals(product.getCreatedBy().getId(), currentUser.getId())) {
            throw new AccessDeniedException("You do not have permission to view the status of this product.");
        }

        return productUtilsMapper.mapToModerationStatusDTO(product);
    }


    @Transactional(readOnly = true)
    public PageDTO<ProductModerationListDTO> findMyRejectedProduct(User currentUser, Integer page, Integer size) {
        Sort sort = Sort.by(LongIdAbstract.Fields.id);
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        Page<Product> rejectedProductPages = productRepository
                .findUserProductsByStatus(currentUser, Status.REJECTED, pageRequest);

        Page<ProductModerationListDTO> map = rejectedProductPages.map(productUtilsMapper::mapToRejectedProductListItemDTO);

        return new PageDTO<>(
                map.getContent(),
                map.getNumber(),
                map.getSize(),
                map.getTotalElements(),
                map.getTotalPages(),
                map.isLast(),
                map.isFirst(),
                map.getNumberOfElements(),
                map.isEmpty()
        );


    }


}