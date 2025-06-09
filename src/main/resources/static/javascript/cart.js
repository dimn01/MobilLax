document.addEventListener("DOMContentLoaded", async () => {
  await loadCart();

  // 전체 비우기 버튼
  document.getElementById("clearCartBtn")?.addEventListener("click", async () => {
    if (confirm("정말로 장바구니를 비우시겠습니까?")) {
      await fetch("/api/cart/clear", { method: "DELETE" });
      await loadCart();
    }
  });

  // 그룹별 결제 및 삭제 처리
  document.querySelector(".cart-list")?.addEventListener("click", async (e) => {
    const groupId = e.target.dataset.groupid;

    if (!groupId) return;

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
      await processGroupPayment(groupId);
    }
  });
});

// 공통 결제 처리 함수
aasync function processGroupPayment(groupId) {
   try {
     const groupRes = await fetch(`/payment/sdk-ready/${groupId}`, { method: "POST" });
     const payments = await groupRes.json();

     for (const [transportType, data] of Object.entries(payments)) {
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

       if (response.code === "USER_CANCEL") {
         alert("사용자가 결제를 취소했습니다.");
         return false; // ❌ 전체 결제 중단
       }

       if (response.code !== undefined) {
         await fetch("/payment/fail", {
           method: "POST",
           headers: { "Content-Type": "application/json" },
           body: JSON.stringify({ paymentId, groupId, amount, transportType })
         });

         alert(`${transportType} 결제 실패: ${response.message}`);
         return false; // ❌ 전체 결제 중단
       }

       await fetch("/payment/complete", {
         method: "POST",
         headers: { "Content-Type": "application/json" },
         body: JSON.stringify({ paymentId, groupId, amount, transportType })
       });

       alert(`${transportType} 결제 완료`);
     }

     await loadCart();
     return true; // ✅ 정상 결제

   } catch (err) {
     alert("결제 처리 중 오류: " + err.message);
     return false; // ❌ 전체 결제 중단
   }
}

// 전체 결제 버튼 처리
async function goToPayment() {
  try {
    const res = await fetch("/api/cart/list");
    if (!res.ok) throw new Error("장바구니 불러오기 실패");

    const grouped = await res.json();
    const groupIds = Object.keys(grouped);

    if (groupIds.length === 0) {
      alert("결제할 경로가 없습니다.");
      return;
    }

    for (const groupId of groupIds) {
      await processGroupPayment(groupId);
      if (result === false) {
          alert("결제를 중단했습니다.");
          return;
      }
    }

  } catch (err) {
    alert("전체 결제 중 오류 발생: " + err.message);
  }
}

// 장바구니 목록 불러오기
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
      container.innerHTML = `
        <div class="cart-empty">
          <img src="https://img.icons8.com/ios/100/empty-box.png" alt="empty">
          <p>장바구니가 비어 있습니다</p>
          <button class="find-route-button" onclick="location.href='/home'">경로 찾기</button>
        </div>`;
      actionButtons.style.display = "none";
      return;
    }

    actionButtons.style.display = "flex";

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
