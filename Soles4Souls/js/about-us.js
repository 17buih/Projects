$(document).ready(function () {
    adjustImagesHeight();
});

$(window).on("resize", function () {
    adjustImagesHeight();
}).resize();

// ADJUST THE HEIGHT OF IMAGES BASED ON EACH DESCRIPTION CONTAINERS' HEIGHT
function adjustImagesHeight() {
    let label1 = ["history", "purpose", "impact", "statistics"];
    let heightSize;
    for (let i = 0; i < label1.length; i++) {
        heightSize = document.getElementById('au-' + label1[i] + '-message').offsetHeight;
        $('#au-' + label1[i] + '-img').css('height', heightSize);
    }
}