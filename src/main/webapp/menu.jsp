<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>Menu</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="stylesheet" href="css/menu.css">
    <style>
      /* Custom styles for menu page */
      .menu-container {
        max-width: 800px;
        margin: 50px auto auto;
      }

      .menu-item {
        border: 1px solid #ddd;
        margin-bottom: 15px;
        padding: 10px;
      }

      .item-image {
        max-width: 100%;
        height: auto;
      }
    </style>
</head>
<body>
<nav class="navbar">
    <h1><a class="navbar-brand" href="index.jsp">Coffee Shop</a></h1>
</nav>
<div class="container menu-container">
    <div class="row">
        <div class="col-3">
            <div class="menu-item">
                <img src="img/coffee.webp" alt="Item 1" class="item-image">
                <p>Item 1</p>
                <p> 200ml</p>
                <p> $5.99</p>
            </div>
        </div>
        <div class="col-3">
            <div class="menu-item">
                <img src="img/coffee.webp" alt="Item 2" class="item-image">
                <p>Item 2</p>
                <p> 300ml</p>
                <p> $8.99</p>
            </div>
        </div>
        <div class="col-3">
            <div class="menu-item">
                <img src="img/coffee.webp" alt="Item 3" class="item-image">
                <p>Item 3</p>
                <p> 400ml</p>
                <p> $12.99</p>
            </div>
        </div>
        <div class="col-3">
            <div class="menu-item">
                <img src="img/coffee.webp" alt="Item 3" class="item-image">
                <p>Item 3</p>
                <p> 500ml</p>
                <p> $15.99</p>
            </div>
        </div>
    </div>
</div>
</body>
</html>