//package uz.pdp.backend.olxapp.service;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Sort;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.access.AccessDeniedException;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.multipart.MultipartFile;
//import uz.pdp.backend.olxapp.entity.*;
//import uz.pdp.backend.olxapp.entity.abstractEntity.LongIdAbstract;
//import uz.pdp.backend.olxapp.enums.Role;
//import uz.pdp.backend.olxapp.exception.EntityNotFoundException;
//import uz.pdp.backend.olxapp.mapper.AttachmentMapper;
//import uz.pdp.backend.olxapp.mapper.ProductMapper;
//import uz.pdp.backend.olxapp.payload.*;
//import uz.pdp.backend.olxapp.repository.AttachmentRepository;
//import uz.pdp.backend.olxapp.repository.CategoryRepository;
//import uz.pdp.backend.olxapp.repository.ProductRepository;
//
//import java.io.IOException;
//import java.util.*;
//
//@Service
//@RequiredArgsConstructor
//public class ProductServiceImpl implements ProductService {
//
//    private final ProductRepository productRepository;
//    private final ProductMapper productMapper;
//    private final AttachmentService attachmentService;
//    private final AttachmentMapper attachmentMapper;
//    private final CategoryRepository categoryRepository;
//    private final AttachmentRepository attachmentRepository;
//
//    @Override
//    public PageDTO<ProductDTO> read(Integer page, Integer size) {
//        Sort sort = Sort.by(LongIdAbstract.Fields.id);
//        PageRequest pageRequest = PageRequest.of(page, size, sort);
//
//        Page<Product> productPage = productRepository.findAll(pageRequest);
//
//        return new PageDTO<>(
//                productPage.getContent().stream().map(productMapper::toDto).toList(),
//                productPage.getNumber(),
//                productPage.getSize(),
//                productPage.getTotalElements(),
//                productPage.getTotalPages(),
//                productPage.isLast(),
//                productPage.isFirst(),
//                productPage.getNumberOfElements(),
//                productPage.isEmpty()
//        );
//    }
//
//    @Override
//    public ProductDTO read(Long id) {
//
//        Product product = productRepository.findById(id)
//                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id, HttpStatus.NOT_FOUND));
//
//        return productMapper.toDto(product);
//
//    }
//
//    @Override
//    public ProductDTO increaseViewCount(Long id) {
//        Product product = productRepository.findById(id)
//                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id, HttpStatus.NOT_FOUND));
//
//        product.setViewCounter(product.getViewCounter() + 1);
//        return productMapper.toDto(productRepository.save(product));
//    }
//
//// ProductServiceImpl.java
//
//    @Override
//    @Transactional // Yangi obyekt va unga bog'liq boshqa obyektlarni saqlash uchun tranzaksiya muhim
//    public ProductDTO save(ProductReqDTO productReqDTO, List<MultipartFile> images) {
//
//        if (images == null || images.isEmpty() || images.size() > 8) {
//            throw new IllegalArgumentException("At least one image is required to create a product.");
//        }
//
//        List<AttachmentDTO> uploadAttachments = attachmentService.upload(images);
//
//        List<Attachment> attachments = uploadAttachments.stream().map(attachmentMapper::toEntity).toList();
//
//        saveMainImage(attachments, productReqDTO.getMainImageIdentifier(), attachmentRepository);
//
//        Category category = categoryRepository.findById(productReqDTO.getCategoryId())
//                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + productReqDTO.getCategoryId(), HttpStatus.NOT_FOUND));
//
//        if (category.getChildren() != null && !category.getChildren().isEmpty()) {
//            throw new IllegalStateException("You can only add products to the final subcategory.");
//        }
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (!(authentication.getPrincipal() instanceof User user)) {
//            throw new AccessDeniedException("User is not authenticated");
//        }
//
//
//        // 2-QADAM: Product ob'ektini yaratish (avvalgidek)
//        Product product = new Product();
//        product.setTitle(productReqDTO.getTitle());
//        product.setDescription(productReqDTO.getDescription());
//        product.setPrice(productReqDTO.getPrice());
//
//        product.setCategory(category);
//        product.setCreatedBy(user);
//
//        // 3-QADAM: ProductImage orqali bog'lash (avvalgidek)
//        boolean isFirstImage = true;
//        for (Attachment attachment : attachments) {
//            ProductImage productImage = new ProductImage();
//            productImage.setAttachment(attachment);
//            productImage.setProduct(product);
//            if (attachment.getOriginalName().equals(productReqDTO.getMainImageIdentifier())) {
//                productImage.setMain(true);
//                isFirstImage = false;
//            }
//            product.getProductImages().add(productImage);
//        }
//
//
//        return productMapper.toDto(productRepository.save(product));
//
//
//    }
//
//
//    /**
//     * Front-end va Back-end kelishuvi qanday bo'ladi:<p>
//     * Front-end'ga vazifa:<p>
//     * Agar foydalanuvchi mavjud rasmni asosiy qilib tanlasa, mainImageIdentifierga shu rasmning IDsini yubor.<p>
//     * Agar foydalanuvchi yangi yuklanayotgan rasmni asosiy qilib tanlasa, mainImageIdentifierga shu faylning originalFilename'ini yubor.<p>
//     *
//     * @param id
//     * @param dto
//     * @param newImages
//     * @return "ProductDTO"
//     */
//// ProductService klassi ichida
//
//    @Override
//    @Transactional(rollbackFor = Exception.class) // Fayl operatsiyalari uchun Exception'ni qo'shib qo'yamiz
//    public ProductDTO updateProduct(Long productId, ProductUpdateDTO dto, List<MultipartFile> newImages) throws IOException {
//        // 1. Product'ni bazadan topamiz
//        Product product = productRepository.findById(productId)
//                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId, HttpStatus.NOT_FOUND));
//
//        // 2. Xavfsizlik tekshiruvi (bu qism o'zgarishsiz qoladi)
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (!(authentication.getPrincipal() instanceof User user)) {
//            throw new AccessDeniedException("User is not authenticated");
//        }
//        if (!product.getCreatedBy().equals(user)) {
//            throw new AccessDeniedException("You are not the owner of this product");
//        }
//
//        // 3. Product'ning asosiy maydonlarini yangilaymiz
//        product.setTitle(dto.getTitle());
//        product.setDescription(dto.getDescription());
//        product.setPrice(dto.getPrice());
//
//        // Category o'zgargan bo'lsa, uni topib o'rnatamiz
//        if (dto.getCategoryId() != null && !dto.getCategoryId().equals(product.getCategory().getId())) {
//            Category category = categoryRepository.findById(dto.getCategoryId())
//                    .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + dto.getCategoryId(), HttpStatus.NOT_FOUND));
//            product.setCategory(category);
//        }
//        // active, isApproved kabi boshqa maydonlarni ham shu yerda yangilash mumkin
//
//        // 4. O'chirilishi kerak bo'lgan rasmlarni aniqlash va o'chirish (Agar DTO'da o'chiriladigan rasmlar ID'si kelsa)
//        if (dto.getImagesToDelete() != null && !dto.getImagesToDelete().isEmpty()) {
//            List<ProductImage> imagesToRemove = new ArrayList<>();
//            for (ProductImage productImage : product.getProductImages()) {
//                if (dto.getImagesToDelete().contains(productImage.getAttachment().getId())) {
//                    imagesToRemove.add(productImage);
//                    // Faylni serverdan ham o'chirish kerak (agar kerak bo'lsa, alohida servisda)
//                    // attachmentService.deleteFile(productImage.getAttachment().getPath());
//                }
//            }
//            product.getProductImages().removeAll(imagesToRemove); // orphanRemoval=true tufayli DBdan o'chadi
//        }
//
//        // 5. Yangi rasmlarni qo'shish
//        if (newImages != null && !newImages.isEmpty()) {
//            // AttachmentService fayllarni yuklab, Attachment entity'larini qaytaradi
//            List<Attachment> savedAttachments = attachmentService.saveAttachments(newImages); // Fayllarni saqlab, Attachment listini olamiz
//
//            for (Attachment attachment : savedAttachments) {
//                ProductImage productImage = new ProductImage();
//                productImage.setProduct(product);
//
//                productImage.setAttachment(attachment);
//                product.getProductImages().add(productImage);
//            }
//        }
//
//        // 6. Asosiy rasmni (isMain) yangilash logikasi
//        updateMainImage(product, dto.getMainImageIdentifier());
//
//
//        // 7. Yangilangan product'ni saqlash
//        Product updatedProduct = productRepository.save(product);
//
//        // 8. Natijani DTO'ga o'girib qaytarish
//        return productMapper.toDto(updatedProduct);
//    }
//
//
//    /**
//     * Product'ning asosiy rasmini yangilaydigan yordamchi metod.
//     * @param product Yangilanayotgan mahsulot
//     * @param mainImageIdentifier Asosiy rasm bo'lishi kerak bo'lgan attachment ID'si yoki fayl nomi
//     */
//    private void updateMainImage(Product product, String mainImageIdentifier) {
//        if (mainImageIdentifier == null || mainImageIdentifier.isBlank()) {
//            // Agar asosiy rasm belgilanmagan bo'lsa, mavjud rasmlardan birinchisini asosiy qilamiz
//            if (!product.getProductImages().isEmpty()) {
//                product.getProductImages().forEach(pi -> pi.setMain(false)); // Avval hammasini false qilamiz
//                product.getProductImages().get(0).setMain(true); // Birinchisini true qilamiz
//            }
//            return;
//        }
//
//        boolean mainImageSet = false;
//
//        // Barcha rasmlarni aylanib chiqamiz
//        for (ProductImage productImage : product.getProductImages()) {
//            Attachment attachment = productImage.getAttachment();
//
//            // `mainImageIdentifier` attachment ID'si yoki original fayl nomi bo'lishi mumkin. Ikkalasiga ham tekshiramiz.
//            boolean isThisTheMainImage = mainImageIdentifier.equals(String.valueOf(attachment.getId())) ||
//                    mainImageIdentifier.equals(attachment.getOriginalName());
//
//            if (isThisTheMainImage) {
//                productImage.setMain(true);
//                mainImageSet = true;
//            } else {
//                productImage.setMain(false);
//            }
//        }
//
//        // Agar foydalanuvchi ko'rsatgan rasm topilmasa (masalan, noto'g'ri id/nom kiritgan bo'lsa)
//        // yoki umuman rasmlar qolmagan bo'lsa, xatolik berish yoki birinchisini asosiy qilib qo'yish mumkin.
//        if (!mainImageSet && !product.getProductImages().isEmpty()) {
//            product.getProductImages().get(0).setMain(true);
//        }
//    }
//
//
//    @Override
//    @Transactional
//    public void updateStatus(Long id, boolean active) {
//
//        Product product = productRepository.findById(id)
//                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id, HttpStatus.NOT_FOUND));
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication.getPrincipal() instanceof User user) {
//            if (!user.getId().equals(product.getCreatedBy().getId())) {
//                throw new AccessDeniedException("You are not the owner of this product");
//            }
//        } else {
//            throw new AccessDeniedException("User is not authenticated");
//        }
//
//        if (product.isActive() == active) {
//            return;
//        }
//
//        product.setActive(active);
//
//        productRepository.save(product);
//    }
//
//    @Override
//    public void deleteProduct(Long id) {
//
//        Product product = productRepository.findById(id)
//                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id, HttpStatus.NOT_FOUND));
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (!(authentication.getPrincipal() instanceof User user)) {
//            throw new AccessDeniedException("User is not authenticated");
//        }
//        if (!user.getId().equals(product.getCreatedBy().getId())) {
//            throw new AccessDeniedException("You are not the owner of this product");
//        }
//        for (Attachment attachment : product.getAttachments()) {
//
//            attachmentService.deleteById(attachment.getId());
//
//        }
//
//        productRepository.delete(product);
//
//    }
//
//    @Override
//    public void approveProduct(Long id) {
//
//        Product product = productRepository.findById(id)
//                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id, HttpStatus.NOT_FOUND));
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (!(authentication.getPrincipal() instanceof User user)) {
//            throw new AccessDeniedException("User is not authenticated");
//        }
//        if (!user.getRole().equals(Role.ADMIN)) {
//            throw new AccessDeniedException("Only admins can approve products");
//        }
//
//        product.setIsApproved(true);
//        productRepository.save(product);
//
//    }
//
//    private void updateMainImage(List<Attachment> attachments, String mainImageIdentifier) {
//        // Avval barchasini "asosiy emas" qilib belgilaymiz
//        attachments.forEach(att -> att.setIsMain(false));
//
//        if (mainImageIdentifier != null && !mainImageIdentifier.isBlank()) {
//            Optional<Attachment> mainAttachmentOpt = attachments.stream()
//                    .filter(att -> {
//                        // Agar identifikator ID bo'lsa (eski rasm)
//                        if (att.getId() != null && mainImageIdentifier.equals(String.valueOf(att.getId()))) {
//                            return true;
//                        }
//                        // Agar identifikator fayl nomi bo'lsa (yangi rasm)
//                        if (att.getOriginalName() != null && mainImageIdentifier.equals(att.getOriginalName())) {
//                            return true;
//                        }
//                        return false;
//                    })
//                    .findFirst();
//
//            if (mainAttachmentOpt.isPresent()) {
//                mainAttachmentOpt.get().setIsMain(true);
//                return; // Asosiy rasm topildi va belgilandi, chiqib ketamiz
//            }
//        }
//
//        // Agar asosiy rasm ko'rsatilmagan bo'lsa yoki topilmasa,
//        // va ro'yxatda rasmlar bo'lsa, birinchisini asosiy qilib qo'yamiz.
//        if (!attachments.isEmpty()) {
//            attachments.get(0).setIsMain(true);
//        }
//        attachmentRepository.saveAll(attachments);
//    }
//
//
//    /**
//     * Attachment ro'yxatidan bitta rasmni asosiy qilib belgilaydigan yordamchi metod.
//     * Bu metodni ham save, ham update metodlarida qayta ishlatish mumkin.
//     */
//    public static void saveMainImage(List<Attachment> attachments, String mainImageIdentifier, AttachmentRepository attachmentRepository) {
//        if (attachments == null || attachments.isEmpty()) {
//            return;
//        }
//
//        // Dastlab barchasini "asosiy emas" qilib qo'yamiz
//        attachments.forEach(att -> att.setIsMain(false));
//
//        Optional<Attachment> mainAttachmentOpt = Optional.empty();
//
//        // Agar front-end aniq bir rasmni ko'rsatgan bo'lsa...
//        if (mainImageIdentifier != null && !mainImageIdentifier.isBlank()) {
//            mainAttachmentOpt = attachments.stream()
//                    .filter(att -> mainImageIdentifier.equals(att.getOriginalName()))
//                    .findFirst();
//        }
//
//        // Agar aniq rasm topilsa, uni asosiy qilamiz.
//        // Aks holda (yoki front-end ko'rsatmagan bo'lsa), ro'yxatdagi birinchi rasmni asosiy qilamiz.
//        if (mainAttachmentOpt.isPresent()) {
//            mainAttachmentOpt.get().setIsMain(true);
//        } else {
//            attachments.get(0).setIsMain(true);
//        }
//        attachmentRepository.save(mainAttachmentOpt.get());
//    }
//}
