const express = require("express");
const http = require("http");
const mysql = require('mysql');
const crypto = require('crypto');
const session = require('express-session');
const MySQLStore = require('express-mysql-session')(session);

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

server.use(express.json());
server.use(sessions);
server.use(initSession);

server.get("/account", checkLogged);
server.post("/message", logMessage);
server.post("/signup", registerUser);
server.post("/login", login);
server.get("/logout", logout);

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
            else res.status(200).send("User Created. Well done!");
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
            });
        });
    });
}
function login(req, res, next){
    let username = req.body.user;
    let password = req.body.pass;
    req.app.locals.pool.query("SELECT hash, salt FROM userInfo WHERE username= '"+username+"';",function(err, result){
        if (err) throw err;
        if(result.length==0) res.status(200).send("User Doesn't Exist!")
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
                    res.status(200).send("Wrong Password!");
                }
            });

        };
    });
}
function initSession(req, res, next){
    if (!req.session.hasOwnProperty("loggedIn")) {
        req.session.loggedIn = false;
    }
    next();
}
