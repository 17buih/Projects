var x = document.getElementsByClassName("hover-item");

var helper = ["state", "address", "city", "contact", "msg"];
let json = "{";
for (let i = 0; i < x.length; i++) {
    json += '"a' + i + '" : {';
    for (let j = 0 ; j < x[i].children.length; j++) {
        if (helper[j] != null) {
            json += '"' + helper[j] + '": "' + x[i].children[j].innerHTML.replace(/<\/?[^>]+(>|$)/g, "") + '"';
            if (j != x[i].children.length-1) {
                json += ',';
            }
        }
    }
    json += "}";
    if (i != x.length-1) {
        json += ",";
    }
}
json += "}";

console.log(json);