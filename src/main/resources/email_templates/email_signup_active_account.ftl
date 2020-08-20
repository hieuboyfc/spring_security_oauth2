<html>
    <body>
		<p><h3>Xin chào ${USERNAME},<h3></p></ br>
		<p>Hệ thống <b>${CURRENT_APP}</b> xin thông báo: "Quý khách đã đăng ký vào ứng dụng ${APP_CODE} vào lúc ${CURRENT_DATE} bằng IP: ${CURRENT_IP}"</p></ br>
		<p>Để kích hoạt tài khoản và đổi mật khẩu vui lòng truy cập vào đường dẫn bên dưới (lưu ý: đường dẫn có giá trị sử dụng trong vòng 7 ngày kể từ lúc nhận email này):</p>
		<a href="${ACCOUNT_ACTIVE_LINK}">${ACCOUNT_ACTIVE_LINK}</a></ br></ br>
		<p>Tài khoản này được sử dụng để đăng nhập hệ thống: ${APP_CODE}</p>
		<p>Đơn vị vận hành hệ thống <b>${CURRENT_APP}</b> kính báo!</p></ br>
		<strong>${CURRENT_SIGNATURE}</strong>
	</body>
</html>