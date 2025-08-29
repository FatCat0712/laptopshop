$(document).ready(function () {
       let currentPath = window.location.pathname.replace(/\/$/, ""); // remove trailing slash
       $(".navbar .nav-link").each(function () {
           let linkPath = $(this).attr("href").replace(/\/$/, "");
           if (window.location.pathname.replace(/\/$/, "") === linkPath) {
               $(this).addClass("active");
           } else {
               $(this).removeClass("active");
           }
       });
});