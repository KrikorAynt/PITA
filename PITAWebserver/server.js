const express = require("express");
const http = require("http");
const mysql = require('mysql');
const crypto = require('crypto');
const session = require('express-session');
const MySQLStore = require('express-mysql-session')(session);
const fs = require('fs');

let options = {
    host: 'localhost',
    port: 3306,
    user: 'root',
    password: 'password',
    database: 'theData'
}

let server = express();
setUpDatabase(server);

const sessionStore = new MySQLStore(options);
const sessions = session({
    key: 'session',
    secret: 'fdghfgthfgfghhjhjhsgfddgf',
    store: sessionStore,
    resave: false,
    saveUninitialized: false,
    cookie: {maxAge: 7200000}
});

server.use(express.json({limit: '1000mb'}));
server.use(express.urlencoded({limit: '1000mb'}));
server.use(sessions);
server.use(initSession);

server.get("/account", checkLogged);
server.post("/message", logMessage);
server.post("/signup", registerUser);
server.post("/login", login);
server.get("/logout", logout);
server.post("/sendVid", receiveVideo);
server.get("/reqVid", sendVideo);
server.get("/reqVidList", sendVideoList);

http.createServer(server).listen(5000);

function logout(req, res, next){
    req.session.loggedIn = false;
    req.session.username = undefined;
    res.status(200).send("You are logged out!");
}
function checkLogged(req, res, next){
    if(req.session.loggedIn==true) res.status(200).send("You Are Logged in as: "+req.session.username);
    else res.status(200).send("You are not logged in!");
}
function logMessage(req, res, next){
    console.log(req.body.message);
    res.status(200).send("Test");
}
function registerUser(req, res, next){
    console.log("Making Acc");
    let username = req.body.user;
    let password = req.body.pass;

    let salt = crypto.randomBytes(16);
    crypto.scrypt(password,salt,128,(err, hash) =>{
        if(err) throw err;
        salt = salt.toString("hex");
        hash = hash.toString("hex");
        req.app.locals.pool.query("INSERT INTO userInfo (username, hash, salt) VALUES ('"+username+"', '"+hash+"','"+salt+"');",function(err, result){
            if (err) res.status(200).send("User already exists. Try again!");
            else{ 
                res.status(200).send("User Created. Well done!");
                fs.mkdirSync("./videos/"+username, { recursive: true })
            }
        });
        
    }); 
}

function setUpDatabase(server){
    
    let con = mysql.createConnection({
        host: "localhost",
        user: "root",
        password: "password"
    });

    con.connect(function(err) {
        if (err) throw err;
        console.log("Connected!");
        let sql = "CREATE DATABASE IF NOT EXISTS theData"
        con.query(sql, function (err, result) {
            if (err) throw err;
            console.log("Database created");
            con.query("USE theData", function(err,result){
                let tabler = "CREATE TABLE IF NOT EXISTS userInfo "+
                    "(username varchar(256) NOT NULL, hash varchar(256) NOT NULL,"+
                    " salt varchar(32) NOT NULL, PRIMARY KEY(username));";
                con.query(tabler, function (err, result) {
                    if (err) throw err;
                    server.locals.pool = mysql.createPool({
                        connectionLimit : 100,
                        host     : 'localhost',
                        user     : 'root',
                        password : 'password',
                        database : 'theData',
                        debug    :  false
                    })
                });
                tabler = "CREATE TABLE IF NOT EXISTS videos "+
                    "(username varchar(256) NOT NULL, title varchar(256) NOT NULL,"+
                    " path varchar(256) NOT NULL, PRIMARY KEY(username, title));";
                con.query(tabler, function (err, result) {
                    if (err) throw err;
                    server.locals.pool = mysql.createPool({
                        connectionLimit : 100,
                        host     : 'localhost',
                        user     : 'root',
                        password : 'password',
                        database : 'theData',
                        debug    :  false
                    })
                });
            });
        });
    });
}
function login(req, res, next){
    let username = req.body.user;
    let password = req.body.pass;
    req.app.locals.pool.query("SELECT hash, salt FROM userInfo WHERE username= '"+username+"';",function(err, result){
        if (err) throw err;
        if(result.length==0) res.status(200).send("Wrong Credentials!");
        else{ 
            let storedHash=result[0].hash;
            let salt = Buffer.from(result[0].salt,"hex");

            crypto.scrypt(password,salt,128,(err,hash)=>{
                if (err) throw err;
                if(hash.toString("hex")==storedHash){
                    req.session.loggedIn=true;
                    req.session.username=username;
                    res.status(200).send("Logged In!");
                }
                else {
                    req.session.loggedIn=false;
                    res.status(200).send("Wrong Credentials!");
                }
            });

        };
    });
}
function receiveVideo(req,res, next){
    let title = req.body.title;
    let vid = req.body.vid;
    let video = Buffer.from(vid,"base64");

    if (req.session.loggedIn==true){
        let path  = "./videos/"+req.session.username+"/"+title;
        fs.writeFile(path, video, function(err){
                if(err) throw err;
                else{
                    req.app.locals.pool.query("INSERT INTO videos (username, title, path) VALUES ('"+req.session.username+"', '"+title+"','"+path+"');",function(err, result){
                        if (err) res.status(200).send("Video Exists!");
                        else res.status(200).send("Video Accepted!");
                    });
                    
                }
        })
    }
    else res.status(401).send("You are not logged in!");

}
function sendVideo(req,res, next){
    let title = req.query.title;
    if (req.session.hasOwnProperty("loggedIn")){
        let path  = "./videos/"+req.session.username+"/"+title;
	    fs.readFile(path, function (err, data) {
            if (err) {
                res.status(200).send("Requested Video DNE");
            } else {
                let video = Buffer.from(data).toString('base64');
                res.status(200).send(video);
            }
        });
    }
    else res.status(200).send("You are not logged in!");
}
function sendVideoList(req, res, next) {
    if (req.session.hasOwnProperty("loggedIn")) {
      let user = req.session.username;
      req.app.locals.pool.query(
        "SELECT title FROM videos WHERE username= '" + user + "';",
        function (err, result) {
          if (err) throw err;
          if (result.length == 0) res.status(200).send("User Has No Videos");
          else {
            let titles = result.map((row) => row.title);
            let titlesString = titles.join(",");
            res.status(200).send(titlesString);
          }
        }
      );
    } else {
      res.status(200).send("You are not logged in!");
    }
  }
  
function initSession(req, res, next){
    if (!req.session.hasOwnProperty("loggedIn")) {
        req.session.loggedIn = false;
    }
    next();
}
