$(document).ready(function () {
    $("#link2-donation").click(function () {
        $('body, html').animate({
            scrollTop: $("#donation-info").offset().top
        }, 900);
    });

    // lists out all types of donation
    let result = "";
    let shoeType = { "Money": "money", "Schedule a Pickup": "phone", "Send Shoes Yourself": "heart" };
    for (let i = 0; i < Object.keys(shoeType).length; i++) {
        let key = Object.keys(shoeType)[i];
        let value = shoeType[key];

        result += '<div class="donation-box">';
        result += '    <div class="donation-box-emoji">';
        result += '        <img src="images/icon/donation-' + value + '.png" class="full" />';
        result += '    </div>';
        result += '    <div class="donation-box-image-text">' + key + '</div>';
        result += '</div>';
    }
    document.getElementById("donation-type-container").innerHTML = result;



    $(".money-selection").click(function () {
        $(".money-selection").removeClass("dark-green-background");
        this.classList.add("dark-green-background");
    });
    $(".payment-recurrence").click(function () {
        $(".payment-recurrence").removeClass("dark-green-background");
        this.classList.add("dark-green-background");
    });

    $(".isShowable").hide();
    $("#money-tab").hide();
    $("#pick-up").hide();

    // when user click
    $(".donation-box:nth-child(1)").click(function () {
        $(".isShowable").show();
        $("#pick-up").hide();
        $("#money-tab").show();

        $('body, html').animate({
            scrollTop: $("#money-tab").offset().top + 3
        }, 900);
    });

    $(".donation-box:nth-child(2)").click(function () {
        $(".isShowable").show();
        $("#money-tab").hide();
        $("#pick-up").show();

        $('body, html').animate({
            scrollTop: $("#pick-up").offset().top + 3
        }, 900);
    });

    $(".donation-box:nth-child(3)").click(function () {
        $(".isShowable").hide();
        $("#money-tab").hide();
        $("#pick-up").hide();

        $('body, html').animate({
            scrollTop: $("#contact-info").offset().top
        }, 900);
    });

    adjustDonationBoxSize();
});


$(window).on("resize", function () {
    adjustDonationBoxSize();
}).resize();


function adjustDonationBoxSize() {
    // adjusting the size based on user's browser & when resizing the browser
    var box = $('.donation-box');
    var width = box.width();
    var circle = $(".donation-box-emoji");
    box.css('height', width);
    circle.css('width', width * 0.7);
    circle.css('height', width * 0.7);
    circle.css('font-size', width / 3);
};