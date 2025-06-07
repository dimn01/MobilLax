document.addEventListener("DOMContentLoaded", () => {
  const container = document.querySelector(".cart-list");
  const cartItems = JSON.parse(localStorage.getItem("cartItems") || "[]");

  console.log("장바구니 아이템:", cartItems);

  if (cartItems.length === 0) {
    container.innerHTML = `
      <div class="cart-empty">
        <img src="https://img.icons8.com/ios/100/empty-box.png" alt="empty">
        <p>장바구니가 비어 있습니다</p>
        <button class="find-route-button" onclick="location.href='/route.html'">경로 찾기</button>
      </div>`;
    return;
  }

  cartItems.forEach((item, idx) => {
    const card = document.createElement("div");
    card.classList.add("cart-card");

    card.innerHTML = `
      <div class="cart-summary">
        <div class="route-type">${item.title}</div>
        <button class="delete-button" data-index="${idx}">삭제</button>
      </div>
      <div class="cart-summary-line">${item.desc}</div>
    `;

    container.appendChild(card);
  });

  container.addEventListener("click", e => {
    if (e.target.classList.contains("delete-button")) {
      const idx = Number(e.target.dataset.index);
      const newCart = cartItems.filter((_, i) => i !== idx);
      localStorage.setItem("cartItems", JSON.stringify(newCart));
      location.reload();
    }
  });
});