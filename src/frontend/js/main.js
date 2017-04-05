import AppView from "./views/app"
import API from "./api"


var api = new API();
var app = new AppView(api);
app.render();
