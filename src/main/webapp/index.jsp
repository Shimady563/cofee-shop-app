<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>Coffee Shop</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="stylesheet" href="css/index.css">
    <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.9.2/dist/umd/popper.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
    <style>
        .nav-link {
          color: black;
        }
    </style>
</head>
<body>

<nav class="navbar navbar-expand navbar-light" style="background-color: darkgray">
    <a class="navbar-brand" href="#">Coffee Shop</a>
    <div class="collapse navbar-collapse justify-content-center m-md-2" id="navbarNav">
        <ul class="navbar-nav">
            <li class="nav-item">
                <a class="nav-link" href="#">Home</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="#">Profile</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="#">News</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="#"></a>
            </li>
        </ul>
    </div>
    <div class="navbar-nav ml-auto">
        <a class="nav-item nav-link" href="#">Sign In</a>
    </div>
</nav>
<div class="main-content">
    <h1 style="text-align: center; margin: 10px">Menu</h1>
    <div class="text-center">
        <a href="#">
            <img src="img/coffee.webp" alt="cup" style="max-width: 100%; border-radius: 20px;">
        </a>
    </div>
</div>
</body>
</html>
