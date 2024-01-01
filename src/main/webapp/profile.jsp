<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>Profile</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="stylesheet" href="css/profile.css">
</head>
<body>
<nav class="navbar">
    <a href="home"><h1 class="navbar-brand">Coffee Shop</h1></a>
    <div class="navbar-nav ml-auto">
        <a class="nav-item nav-link" href="sign-out">Sign Out</a>
    </div>
</nav>
<div class="container mt-5">
    <div class="row">
        <div class="col-md-4">
            <h2>Profile</h2>
            <ul class="list-group">
                <li class="list-group-item"><a href="news.jsp">News</a></li>
                <li class="list-group-item"><a href="favorites.jsp">Favorites</a></li>
                <li class="list-group-item"><a href="menu.jsp">Menu</a></li>
            </ul>
        </div>
        <div class="col-md-8">
            <h2>Change Username/Password</h2>
            <form action="profile" method="post">
                <div class="form-group">
                    <label for="newLogin">Username:</label>
                    <input type="text" class="form-control" id="newLogin" name="newLogin" value="${username}" required>
                </div>
                <div class="form-group">
                    <label for="newPassword">Password:</label>
                    <input type="password" class="form-control" id="newPassword" name="newPassword">
                </div>
                <button type="submit" class="btn btn-primary"  style="background-color: black; border-color: black">Update</button>
            </form>
        </div>
    </div>
</div>
</body>
</html>
