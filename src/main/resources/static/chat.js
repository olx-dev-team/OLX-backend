// 🔐 TOKENNI SHU YERGA YOZING
const token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwiaWF0IjoxNzUwODYxMjQ0LCJleHAiOjE3NTA4NjQ4NDR9.CckVdwkeUS9tcsMzqjhADRQXrukYZ1lBpFUF1nG4uHk";

let selectedChatId = null;

function fetchChats() {
    fetch("/api/chat", {
        headers: {
            "Authorization": token
        }
    })
        .then(res => res.json())
        .then(chats => renderChatList(chats))
        .catch(err => console.error("Chatlar yuklanmadi:", err));
}

function renderChatList(chats) {
    const container = document.getElementById("chatsContainer");
    container.innerHTML = "";

    if (chats.length === 0) {
        container.innerHTML = `
      <div class="no-chats">
        <img src="/chat-icon.png" alt="No messages" />
        <h3>Xabar yo‘q</h3>
        <p>Kim bilandir yozishsangiz, shu yerda paydo bo‘ladi.</p>
      </div>
    `;
        return;
    }

    chats.forEach(chat => {
        const div = document.createElement("div");
        div.className = "chat-item" + (chat.lastMessage && !chat.lastMessage.read ? " unread" : "");
        div.innerHTML = `
      <strong>${chat.companion.name}</strong><br>
      <small>${chat.lastMessage?.content || "Xabar yo‘q"}</small>
    `;
        div.onclick = () => selectChat(chat.id);
        container.appendChild(div);
    });
}

function selectChat(chatId) {
    selectedChatId = chatId;
    document.getElementById("noMessages").classList.add("hidden");
    document.getElementById("messagesBox").classList.remove("hidden");
    fetchMessages(chatId);
}

function fetchMessages(chatId) {
    fetch(`/api/chat/${chatId}/messages`, {
        headers: {
            "Authorization": token
        }
    })
        .then(res => res.json())
        .then(data => renderMessages(data.content))
        .catch(err => console.error("Xabarlarni yuklashda xatolik:", err));
}

function renderMessages(messages) {
    const container = document.getElementById("messagesContainer");
    container.innerHTML = "";

    messages.reverse().forEach(msg => {
        const div = document.createElement("div");
        div.className = "message";
        div.innerHTML = `
      <strong>${msg.senderId}:</strong> ${msg.content}
      <br><small>${new Date(msg.sentAt).toLocaleTimeString()}</small>
    `;
        container.appendChild(div);
    });

    container.scrollTop = container.scrollHeight;
}

document.getElementById("messageForm").addEventListener("submit", e => {
    e.preventDefault();
    const text = document.getElementById("messageInput").value.trim();
    if (!text || !selectedChatId) return;

    fetch(`/api/chat/${selectedChatId}/message`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": token
        },
        body: JSON.stringify({ content: text })
    })
        .then(res => {
            if (!res.ok) throw new Error("Xabar yuborishda xatolik");
            document.getElementById("messageInput").value = "";
            fetchMessages(selectedChatId);
            fetchChats();
        })
        .catch(err => console.error(err));
});

// Har 10 sekundda chatlarni yangilash
setInterval(fetchChats, 10000);
fetchChats();
