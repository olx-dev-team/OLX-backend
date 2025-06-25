document.addEventListener('DOMContentLoaded', () => {

    // HTML elementlarini tanlab olish
    const fetchBtn = document.getElementById('fetch-btn');
    const updateForm = document.getElementById('product-update-form');
    const responseOutput = document.getElementById('response-output');

    // Rasm konteynerlari
    const existedImagesContainer = document.getElementById('existed-images-container');
    const newImagesContainer = document.getElementById('new-images-container');
    const addNewImageBtn = document.getElementById('add-new-image-btn');

    let newImageCounter = 0; // Yangi rasmlarni sanash uchun

    // 1. "Ma'lumotni Yuklash" tugmasi bosilganda
    fetchBtn.addEventListener('click', async () => {
        const productId = document.getElementById('product-id').value;
        const token = document.getElementById('auth-token').value;
        const baseUrl = document.getElementById('api-base-url').value;

        if (!productId || !token) {
            alert("Iltimos, Mahsulot ID va Tokenni kiriting.");
            return;
        }

        responseOutput.textContent = "Yuklanmoqda...";
        try {
            const response = await fetch(`${baseUrl}${productId}`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });

            if (!response.ok) {
                throw new Error(`Xatolik: ${response.status} ${response.statusText}`);
            }

            const data = await response.json();
            populateForm(data);
            responseOutput.textContent = "Ma'lumotlar muvaffaqiyatli yuklandi. Tahrirlashingiz mumkin.";
            updateForm.classList.remove('hidden');

        } catch (error) {
            responseOutput.textContent = `Xatolik yuz berdi: ${error.message}`;
            updateForm.classList.add('hidden');
        }
    });

    // 2. Formani backend'dan kelgan ma'lumotlar bilan to'ldirish
    function populateForm(productData) {
        document.getElementById('form-product-id').value = productData.id;
        document.getElementById('title').value = productData.title;
        document.getElementById('description').value = productData.description;
        document.getElementById('price').value = productData.price;
        document.getElementById('active').checked = productData.active;

        // Mavjud rasmlarni tozalab, yangilarini chizish
        existedImagesContainer.innerHTML = '';
        productData.productImages.forEach(img => {
            const imageCard = document.createElement('div');
            imageCard.className = 'image-card';
            // Bu `ProductImage`ning ID'sini saqlab qo'yamiz.
            imageCard.dataset.imageId = img.id;

            imageCard.innerHTML = `
                <img src="/api/attachments/${img.attachmentId}" alt="Product Image">
                <div>
                    <input type="radio" name="main-image-selector" class="main-radio" value="existed_${img.id}" ${img.main ? 'checked' : ''}>
                    <label>Asosiy</label>
                </div>
                <button type="button" class="btn btn-danger remove-btn">O'chirish</button>
            `;
            existedImagesContainer.appendChild(imageCard);
        });

        // "O'chirish" tugmasi uchun event listener
        existedImagesContainer.querySelectorAll('.remove-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                // Rasm kartasini DOM'dan o'chiramiz
                e.target.closest('.image-card').remove();
            });
        });
    }

    // 3. "Yangi Rasm Qo'shish" tugmasi
    addNewImageBtn.addEventListener('click', () => {
        newImageCounter++;
        const newImageCard = document.createElement('div');
        newImageCard.className = 'image-card new-image-card';
        newImageCard.dataset.newImageId = newImageCounter;

        newImageCard.innerHTML = `
            <input type="file" name="new-image-file" class="new-image-input" required>
            <div>
                <input type="radio" name="main-image-selector" class="main-radio" value="new_${newImageCounter}">
                <label>Asosiy</label>
            </div>
            <button type="button" class="btn btn-danger remove-btn">O'chirish</button>
        `;
        newImagesContainer.appendChild(newImageCard);

        // Yangi qo'shilgan "O'chirish" tugmasi uchun ham listener
        newImageCard.querySelector('.remove-btn').addEventListener('click', (e) => {
            e.target.closest('.image-card').remove();
        });
    });

    // 4. Formani jo'natish (submit)
    updateForm.addEventListener('submit', async (e) => {
        e.preventDefault(); // Brauzerning standart jo'natishini to'xtatish

        const productId = document.getElementById('form-product-id').value;
        const token = document.getElementById('auth-token').value;
        const baseUrl = document.getElementById('api-base-url').value; // URLni qayta olamiz

        // FormData - fayllarni jo'natish uchun ideal
        const formData = new FormData();

        // Asosiy ma'lumotlarni qo'shish
        formData.append('title', document.getElementById('title').value);
        formData.append('description', document.getElementById('description').value);
        formData.append('price', document.getElementById('price').value);
        formData.append('active', document.getElementById('active').checked);

        // Mavjud (o'chirilmagan) rasmlarni DTO formatida qo'shish
        const existedImageElements = existedImagesContainer.querySelectorAll('.image-card');
        existedImageElements.forEach((card, index) => {
            const imageId = card.dataset.imageId;
            const isMain = card.querySelector('.main-radio').checked;

            formData.append(`existedImages[${index}].imageId`, imageId);
            formData.append(`existedImages[${index}].main`, isMain);
        });

        // Yangi rasmlarni DTO formatida qo'shish
        const newImageElements = newImagesContainer.querySelectorAll('.new-image-card');
        let newImageIndex = 0;
        newImageElements.forEach((card) => {
            const fileInput = card.querySelector('input[type="file"]');
            if (fileInput.files.length > 0) {
                const isMain = card.querySelector('.main-radio').checked;
                formData.append(`productNewImages[${newImageIndex}].file`, fileInput.files[0]);
                formData.append(`productNewImages[${newImageIndex}].main`, isMain);
                newImageIndex++;
            }
        });

        responseOutput.textContent = "Jo'natilmoqda...";

        try {
            const response = await fetch(`${baseUrl}${productId}`, {
                method: 'PUT',
                headers: {
                    // Muhim: FormData bilan 'Content-Type' ni o'zingiz qo'ymang!
                    // Brauzer avtomatik tarzda `multipart/form-data` qilib to'g'ri `boundary` bilan jo'natadi.
                    'Authorization': `Bearer ${token}`
                },
                body: formData
            });

            const result = await response.json();

            if (!response.ok) {
                // Serverdan kelgan xatolikni ko'rsatish (masalan, validatsiya xatolari)
                responseOutput.textContent = `Xatolik (${response.status}):\n${JSON.stringify(result, null, 2)}`;
            } else {
                responseOutput.textContent = `Muvaffaqiyatli yangilandi:\n${JSON.stringify(result, null, 2)}`;
                // Formani qayta yuklash uchun sahifani yangilash mumkin
                // location.reload();
            }

        } catch (error) {
            responseOutput.textContent = `Tizimda xatolik: ${error.message}`;
        }
    });
});