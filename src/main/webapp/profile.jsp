<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>Profile</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
</head>
<body>
<div class="container mt-5">
    <div class="row">
        <div class="col-md-4">
            <h2>Profile</h2>
            <p><strong>User Points:</strong> 1000</p>
            <ul class="list-group">
                <li class="list-group-item"><a href="index.jsp">Home</a></li>
                <li class="list-group-item"><a href="#">Favorite</a></li>
                <li class="list-group-item"><a href="menu.jsp">Menu</a></li>
            </ul>
        </div>
        <div class="col-md-8">
            <h2>Change Username/Password</h2>
            <form action="" method="post">
                <div class="form-group">
                    <label for="newLogin">Username:</label>
                    <input type="text" class="form-control" id="newLogin" name="newLogin" required>
                </div>
                <div class="form-group">
                    <label for="newPassword">Password:</label>
                    <input type="password" class="form-control" id="newPassword" name="newPassword" required>
                </div>
                <div class="form-group">
                    <label for="confirmPassword">Confirm Password:</label>
                    <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" required>
                </div>
                <!-- Element for error messages-->
                <p style="color: red; font-size: 12px; margin: 0">
                </p>
                <button type="submit" class="btn btn-primary"  style="background-color: black; border-color: black">Update</button>
            </form>
        </div>
    </div>
</div>
</body>
</html>
