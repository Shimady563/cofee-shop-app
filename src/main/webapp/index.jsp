<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>Coffee Shop</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="stylesheet" href="css/index.css">
</head>
<body>
<nav class="navbar navbar-expand">
    <h1 class="navbar-brand">Coffee Shop</h1>
    <div class="navbar-collapse justify-content-center m-md-2" id="navbarNav">
        <ul class="navbar-nav">
            <li class="nav-item">
                <a class="nav-link" href="profile">Profile</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="favorites.jsp">Favorites</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="news.jsp">News</a>
            </li>
        </ul>
    </div>
    <div class="navbar-nav ml-auto">
        <!--I had to make this... mess because for some reason I could not include jstl into my project properly-->
        <div class="nav-item nav-link">
            <span>${username}</span>
        ${auth ? "<img src=\"img/coffee-cup.svg\" alt=\"Profile Icon\" style=\"width: 30px; height: 30px; border-radius: 50%;\">" :
                "<a class=\"nav-item nav-link\" href=\"sign-in\">Sign In</a>"}
        </div>
    </div>
</nav>
<div class="main-content">
    <h1 class="menu-title">Menu</h1>
    <div class="text-center">
        <a href="menu.jsp">
            <img class="img img-responsive menu-image" src="img/coffee.webp" alt="cup">
        </a>
    </div>
</div>
</body>
</html>
