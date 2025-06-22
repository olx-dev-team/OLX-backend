package uz.pdp.backend.olxapp.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.pdp.backend.olxapp.entity.*;
import uz.pdp.backend.olxapp.entity.abstractEntity.LongIdAbstract;
import uz.pdp.backend.olxapp.enums.Role;
import uz.pdp.backend.olxapp.exception.EntityNotFoundException;
import uz.pdp.backend.olxapp.mapper.AttachmentMapper;
import uz.pdp.backend.olxapp.mapper.ProductMapper;
import uz.pdp.backend.olxapp.payload.*;
import uz.pdp.backend.olxapp.repository.AttachmentRepository;
import uz.pdp.backend.olxapp.repository.CategoryRepository;
import uz.pdp.backend.olxapp.repository.ProductRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final AttachmentService attachmentService;
    private final AttachmentMapper attachmentMapper;
    private final CategoryRepository categoryRepository;
    private final AttachmentRepository attachmentRepository;

    @Override
    public PageDTO<ProductDTO> read(Integer page, Integer size) {
        Sort sort = Sort.by(LongIdAbstract.Fields.id);
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        Page<Product> productPage = productRepository.findAll(pageRequest);

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

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id, HttpStatus.NOT_FOUND));

        return productMapper.toDto(product);

    }

    @Override
    public ProductDTO increaseViewCount(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id, HttpStatus.NOT_FOUND));

        product.setViewCounter(product.getViewCounter() + 1);
        return productMapper.toDto(productRepository.save(product));
    }

// ProductServiceImpl.java

    @Override
    @Transactional // Yangi obyekt va unga bog'liq boshqa obyektlarni saqlash uchun tranzaksiya muhim
    public ProductDTO save(ProductReqDTO productReqDTO, List<MultipartFile> images) {

        if (images == null || images.isEmpty() || images.get(0).isEmpty()) {
            throw new IllegalArgumentException("At least one image is required to create a product.");
        }

        List<AttachmentDTO> uploadAttachments = attachmentService.upload(images);

        List<Attachment> attachments = uploadAttachments.stream().map(attachmentMapper::toEntity).toList();

        saveMainImage(attachments, productReqDTO.getMainImageIdentifier(), attachmentRepository);

        Category category = categoryRepository.findById(productReqDTO.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + productReqDTO.getCategoryId(), HttpStatus.NOT_FOUND));

        Product product = getProduct(productReqDTO, category, attachments);

        Product save = productRepository.save(product);

        return productMapper.toDto(save);


    }

    private static Product getProduct(ProductReqDTO productReqDTO, Category category, List<Attachment> attachments) {
        if (category.getChildren() != null && !category.getChildren().isEmpty()) {
            throw new IllegalStateException("You can only add products to the final subcategory.");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof User user)) {
            throw new AccessDeniedException("User is not authenticated");
        }


        return new Product(
                productReqDTO.getTitle(),
                productReqDTO.getDescription(),
                productReqDTO.getPrice(),
                false,
                0,
                category,
                new ArrayList<Favorites>(),
                attachments,
                user
        );
    }


    /**
     * Front-end va Back-end kelishuvi qanday bo'ladi:<p>
     * Front-end'ga vazifa:<p>
     * Agar foydalanuvchi mavjud rasmni asosiy qilib tanlasa, mainImageIdentifierga shu rasmning IDsini yubor.<p>
     * Agar foydalanuvchi yangi yuklanayotgan rasmni asosiy qilib tanlasa, mainImageIdentifierga shu faylning originalFilename'ini yubor.<p>
     *
     * @param id
     * @param dto
     * @param newImages
     * @return "ProductDTO"
     */
    @Override
    @Transactional
    public ProductDTO updateProduct(Long id, ProductUpdateDTO dto, List<MultipartFile> newImages) {
        // 1. Product'ni bazadan topamiz
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id, HttpStatus.NOT_FOUND));

        // 2. Xavfsizlik tekshiruvi: foydalanuvchi product egasi ekanligini tekshirish
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof User user)) {
            throw new AccessDeniedException("User is not authenticated");
        }
        if (!user.equals(product.getCreatedBy())) { // Yoki getOwner()
            throw new AccessDeniedException("You are not the owner of this product");
        }


        // 3. Yangi rasmlarni serverga yuklaymiz va Attachment obyektlarini yaratamiz
        List<AttachmentDTO> newAttachments = new ArrayList<>();
        if (newImages != null && !newImages.isEmpty()) {
            // Sizning attachmentService'ingiz fayllarni yuklab, Attachment entity'larini qaytarishi kerak
            newAttachments = attachmentService.upload(newImages); // Bu metod List<Attachment> qaytaradi deb faraz qilamiz
        }

        // 4. Mavjud rasmlarni boshqarish
        List<Attachment> currentAttachments = product.getAttachments();
        List<Long> keptImageIds = dto.getKeptImageIds() != null ? dto.getKeptImageIds() : Collections.emptyList();

        // O'chirilishi kerak bo'lgan fayllarni alohida saqlab qo'yamiz
        List<Attachment> attachmentsToDelete = currentAttachments.stream()
                .filter(att -> !keptImageIds.contains(att.getId()))
                .toList();

        // Saqlanib qoladigan eski rasmlar ro'yxati
        List<Attachment> keptAttachments = currentAttachments.stream()
                .filter(att -> keptImageIds.contains(att.getId()))
                .toList();

        // 5. Product'ning rasm ro'yxatini yakuniy holatga keltiramiz
        List<Attachment> finalAttachments = new ArrayList<>(keptAttachments);
        finalAttachments.addAll(newAttachments.stream().map(attachmentMapper::toEntity).toList());

        // 6. Asosiy (glavniy) rasmni belgilaymiz
        updateMainImage(finalAttachments, dto.getMainImageIdentifier());

        // 7. Product'ning boshqa ma'lumotlarini DTO'dan olib yangilaymiz
        productMapper.updateFromDto(dto, product); // MapStruct yoki o'zingiz yozgan mapper

        // Kategoriya yangilangan bo'lsa
        if (dto.getCategoryId() != null && !dto.getCategoryId().equals(product.getCategory().getId())) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + dto.getCategoryId(), HttpStatus.NOT_FOUND));
            if (!category.getChildren().isEmpty()) {
                throw new IllegalStateException("This category has child categories");
            }
            product.setCategory(category);
        }

        // 8. Product'ga yangilangan rasm ro'yxatini o'rnatamiz
        product.getAttachments().clear();
        product.getAttachments().addAll(finalAttachments);
        // Har bir attachment'ga product'ni bog'laymiz (yangi qo'shilganlar uchun muhim)
//        for (Attachment attachment : finalAttachments) {
//            attachment.setProduct(product);
//        }

        // 9. Product'ni saqlaymiz.
        // CascadeType.ALL va orphanRemoval=true tufayli Hibernate o'zi hamma ishni bajaradi:
        // - Yangi attachment'larni INSERT qiladi.
        // - Eskilaridan o'zgarganlarini UPDATE qiladi.
        // - Ro'yxatdan olib tashlanganlarni (attachmentsToDelete) DELETE qiladi.
        Product updatedProduct = productRepository.save(product);

        // 10. O'chirilgan Attachment'larga tegishli FIZIK fayllarni serverdan o'chiramiz.
        // Bu ishni tranzaksiya muvaffaqiyatli yakunlangandan keyin qilish eng to'g'risi.
//        attachmentService.deletePhysicalFiles(attachmentsToDelete);

        for (Attachment attachment : attachmentsToDelete) {
            attachmentService.deleteById(attachment.getId());
        }
        return productMapper.toDto(updatedProduct);
    }


    @Override
    @Transactional
    public void updateStatus(Long id, boolean active) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id, HttpStatus.NOT_FOUND));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof User user) {
            if (!user.getId().equals(product.getCreatedBy().getId())) {
                throw new AccessDeniedException("You are not the owner of this product");
            }
        } else {
            throw new AccessDeniedException("User is not authenticated");
        }

        if (product.isActive() == active) {
            return;
        }

        product.setActive(active);

        productRepository.save(product);
    }

    @Override
    public void deleteProduct(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id, HttpStatus.NOT_FOUND));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof User user)) {
            throw new AccessDeniedException("User is not authenticated");
        }
        if (!user.getId().equals(product.getCreatedBy().getId())) {
            throw new AccessDeniedException("You are not the owner of this product");
        }
        for (Attachment attachment : product.getAttachments()) {

            attachmentService.deleteById(attachment.getId());

        }

        productRepository.delete(product);

    }

    @Override
    public void approveProduct(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id, HttpStatus.NOT_FOUND));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof User user)) {
            throw new AccessDeniedException("User is not authenticated");
        }
        if (!user.getRole().equals(Role.ADMIN)) {
            throw new AccessDeniedException("Only admins can approve products");
        }

        product.setIsApproved(true);
        productRepository.save(product);

    }

    private void updateMainImage(List<Attachment> attachments, String mainImageIdentifier) {
        // Avval barchasini "asosiy emas" qilib belgilaymiz
        attachments.forEach(att -> att.setIsMain(false));

        if (mainImageIdentifier != null && !mainImageIdentifier.isBlank()) {
            Optional<Attachment> mainAttachmentOpt = attachments.stream()
                    .filter(att -> {
                        // Agar identifikator ID bo'lsa (eski rasm)
                        if (att.getId() != null && mainImageIdentifier.equals(String.valueOf(att.getId()))) {
                            return true;
                        }
                        // Agar identifikator fayl nomi bo'lsa (yangi rasm)
                        if (att.getOriginalName() != null && mainImageIdentifier.equals(att.getOriginalName())) {
                            return true;
                        }
                        return false;
                    })
                    .findFirst();

            if (mainAttachmentOpt.isPresent()) {
                mainAttachmentOpt.get().setIsMain(true);
                return; // Asosiy rasm topildi va belgilandi, chiqib ketamiz
            }
        }

        // Agar asosiy rasm ko'rsatilmagan bo'lsa yoki topilmasa,
        // va ro'yxatda rasmlar bo'lsa, birinchisini asosiy qilib qo'yamiz.
        if (!attachments.isEmpty()) {
            attachments.get(0).setIsMain(true);
        }
    }


    /**
     * Attachment ro'yxatidan bitta rasmni asosiy qilib belgilaydigan yordamchi metod.
     * Bu metodni ham save, ham update metodlarida qayta ishlatish mumkin.
     */
    public static void saveMainImage(List<Attachment> attachments, String mainImageIdentifier, AttachmentRepository attachmentRepository) {
        if (attachments == null || attachments.isEmpty()) {
            return;
        }

        // Dastlab barchasini "asosiy emas" qilib qo'yamiz
        attachments.forEach(att -> att.setIsMain(false));

        Optional<Attachment> mainAttachmentOpt = Optional.empty();

        // Agar front-end aniq bir rasmni ko'rsatgan bo'lsa...
        if (mainImageIdentifier != null && !mainImageIdentifier.isBlank()) {
            mainAttachmentOpt = attachments.stream()
                    .filter(att -> mainImageIdentifier.equals(att.getOriginalName()))
                    .findFirst();
        }

        // Agar aniq rasm topilsa, uni asosiy qilamiz.
        // Aks holda (yoki front-end ko'rsatmagan bo'lsa), ro'yxatdagi birinchi rasmni asosiy qilamiz.
        if (mainAttachmentOpt.isPresent()) {
            mainAttachmentOpt.get().setIsMain(true);
        } else {
            attachments.get(0).setIsMain(true);
        }
        attachmentRepository.save(mainAttachmentOpt.get());
    }
}
