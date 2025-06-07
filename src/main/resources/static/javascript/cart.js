document.addEventListener("DOMContentLoaded", async () => {
  await loadCart();

  // 전체 비우기 버튼
  document.getElementById("clearCartBtn")?.addEventListener("click", async () => {
    if (confirm("정말로 장바구니를 비우시겠습니까?")) {
      await fetch("/api/cart/clear", { method: "DELETE" });
      await loadCart();
    }
  });
  document.querySelector(".cart-list")?.addEventListener("click", async (e) => {
    const groupId = e.target.dataset.groupid;

    // 🧹 삭제
    if (e.target.classList.contains("group-delete-button")) {
      if (confirm("이 경로 묶음을 삭제하시겠습니까?")) {
        try {
          await fetch(`/api/cart/group/${groupId}`, { method: "DELETE" });
          await loadCart();
        } catch (err) {
          alert("삭제 실패: " + err.message);
        }
      }
    }

    // 💳 결제
    if (e.target.classList.contains("group-pay-button")) {
      try {
        // 서버에서 여러 결제 정보 받아오기
        const res = await fetch(`/payment/sdk-ready/${groupId}`, { method: "POST" });
        const payments = await res.json();

        // 각 교통수단마다 순차적으로 결제 진행
        for (const [transport, data] of Object.entries(payments)) {
          const { storeId, channelKey, paymentId, orderName, amount } = data;

          const response = await PortOne.requestPayment({
            storeId,
            channelKey,
            paymentId,
            orderName,
            totalAmount: amount,
            currency: "CURRENCY_KRW",
            payMethod: "CARD"
          });

          if (response.code !== undefined) {
            // 실패 기록
            await fetch("/payment/fail", {
              method: "POST",
              headers: { "Content-Type": "application/json" },
              body: JSON.stringify({
                paymentId,
                groupId,
                amount,
                transportType
              })
            });
            alert(`${transport} 결제 실패: ` + response.message);
            continue;
          }

          // 성공 기록
          await fetch("/payment/complete", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
              paymentId,
              groupId,
              amount,
              transportType
            })
          });

          alert(`${transport} 결제 완료`);
        }
      } catch (err) {
        alert("결제 중 오류 발생: " + err.message);
      }
    }
  });
});

// 장바구니 불러오기
async function loadCart() {
  const container = document.querySelector(".cart-list");
  const actionButtons = document.querySelector(".cart-actions");
  container.innerHTML = "";

  try {
    const res = await fetch("/api/cart/list");
    if (!res.ok) throw new Error("서버 오류");

    const grouped = await res.json();
    const groupIds = Object.keys(grouped);

    if (groupIds.length === 0) {
      // 비어 있을 때
      container.innerHTML = `
        <div class="cart-empty">
          <img src="https://img.icons8.com/ios/100/empty-box.png" alt="empty">
          <p>장바구니가 비어 있습니다</p>
          <button class="find-route-button" onclick="location.href='/home'">경로 찾기</button>
        </div>`;
      actionButtons.style.display = "none";  // 버튼 숨김
      return;
    }

    actionButtons.style.display = "flex";  // 버튼 보임

    groupIds.forEach(groupId => {
      const items = grouped[groupId];
      let total = 0;

      const groupEl = document.createElement("div");
      groupEl.className = "cart-group";
      groupEl.innerHTML = `
        <h3>
          🛍️ 경로 묶음
          <div>
            <button class="group-pay-button" data-groupid="${groupId}">결제하기</button>
            <button class="group-delete-button" data-groupid="${groupId}">삭제</button>
          </div>
        </h3>
      `;

      items.forEach(item => {
        total += item.routePayment;
        const div = document.createElement("div");
        div.className = "cart-item";
        div.innerHTML = `
          <div><strong>${item.startName} → ${item.endName}</strong></div>
          <div>${item.mode} / ${item.route}</div>
          <div>￦${item.routePayment.toLocaleString()}원</div>
        `;
        groupEl.appendChild(div);
      });

      const totalEl = document.createElement("div");
      totalEl.className = "total-fare";
      totalEl.innerHTML = `<strong>합계: ￦${total.toLocaleString()}원</strong>`;
      groupEl.appendChild(totalEl);

      container.appendChild(groupEl);
    });
  } catch (e) {
    console.error("장바구니 불러오기 실패", e);
    container.innerHTML = "<p>장바구니 정보를 불러올 수 없습니다.</p>";
    actionButtons.style.display = "none";
  }
}

// 결제 버튼
function goToPayment() {
  alert("결제 기능은 추후 구현 예정입니다.");
}
