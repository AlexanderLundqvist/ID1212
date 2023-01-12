/*
   Author: Sten Andersson
   Modified by: Alexander Lundqvist

   This file connects do an Apache Derby database client, creates a database for the quiz 
   game and populates it with data.
*/

--DROP TABLE IF EXISTS results;
--DROP TABLE IF EXISTS selector;
--DROP TABLE IF EXISTS questions;
--DROP TABLE IF EXISTS users;
--DROP TABLE IF EXISTS quizzes;

--CONNECT 'jdbc:derby:quizDB;create=true;user=nbuser;password=nbuser';
--CONNECT 'jdbc:derby:quizDB;user=nbuser;password=nbuser';
--CONNECT 'jdbc:derby://localhost:1527/quizDB;create=true;user=nbuser;password=nbuser';
--CONNECT 'jdbc:derby://localhost:1527/quizDB;user=nbuser;password=nbuser';
--CONNECT 'jdbc:derby:C:\Users\Alexander\Software\DerbyDB\quizDB;create=true;user=nbuser;password=nbuser';
--CONNECT 'jdbc:derby:C:\Users\Alexander\Software\DerbyDB\quizDB;user=nbuser;password=nbuser';
-- Above command in NetBeans

CREATE TABLE users (
	id INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
	username VARCHAR(32) UNIQUE NOT NULL,
	password VARCHAR(32) NOT NULL
);
INSERT INTO users (username,password) VALUES ('Alexlu@kth.se', 'Alex');
INSERT INTO users (username,password) VALUES ('Rshojaei@kth.se', 'Ramin');

CREATE TABLE questions (
	id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
	text VARCHAR(64) NOT NULL,
	options VARCHAR(64) NOT NULL,
	answer VARCHAR(64) NOT NULL
);
-- Quiz 1
INSERT INTO questions (text,options,answer) VALUES ('Which planets are larger than earth?', 'Mercury/Mars/Saturn', '0/0/1');
INSERT INTO questions (text,options,answer) VALUES ('Which planet is nearest to the Sun?', 'Mercury/Mars/Saturn', '1/0/0');
INSERT INTO questions (text,options,answer) VALUES ('Which planets have rings?', 'Mercury/Mars/Saturn', '0/0/1');

-- Quiz 2
INSERT INTO questions (text,options,answer) VALUES ('A caracal is what type of animal?', 'Bird/Fish/Cat', '0/0/1');
INSERT INTO questions (text,options,answer) VALUES ('What is the collective name for a group of crows?', 'Flight/Murder/Shoal', '0/1/0');
INSERT INTO questions (text,options,answer) VALUES ('Canis is the latin word for which animal?', 'Rabbit/Dog/Snake', '0/1/0');

-- Quiz 3
INSERT INTO questions (text,options,answer) VALUES ('Which of these cities was NOT founded by the Romans?', 'Alexandria/Cologne/London', '1/0/0');
INSERT INTO questions (text,options,answer) VALUES ('What was the capital of the Byzantine Empire?', 'Jerusalem/Constantinople/Alexandria', '0/1/0');
INSERT INTO questions (text,options,answer) VALUES ('Which era came first?', 'Paleolithic/Neolithic/Chalcolithic', '1/0/0');

CREATE TABLE quizzes (
	id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
	subject VARCHAR(64) NOT NULL
);
INSERT INTO quizzes (subject) VALUES ('Astronomy');
INSERT INTO quizzes (subject) VALUES ('Nature');
INSERT INTO quizzes (subject) VALUES ('History');

CREATE TABLE selector(
	quiz_id INT NOT NULL REFERENCES quizzes(id),
	question_id INT NOT NULL REFERENCES questions(id)
);
-- Quiz 1
INSERT INTO selector (quiz_id, question_id) VALUES (1,1);
INSERT INTO selector (quiz_id, question_id) VALUES (1,2);
INSERT INTO selector (quiz_id, question_id) VALUES (1,3);

-- Quiz 2
INSERT INTO selector (quiz_id, question_id) VALUES (2,4);
INSERT INTO selector (quiz_id, question_id) VALUES (2,5);
INSERT INTO selector (quiz_id, question_id) VALUES (2,6);

-- Quiz 3
INSERT INTO selector (quiz_id, question_id) VALUES (3,7);
INSERT INTO selector (quiz_id, question_id) VALUES (3,8);
INSERT INTO selector (quiz_id, question_id) VALUES (3,9);

CREATE TABLE results(
	id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
	user_id INT NOT NULL REFERENCES users(id),
	quiz_id INT NOT NULL REFERENCES quizzes(id),
	score INT NOT NULL
);
-- User 1
INSERT INTO results (user_id,quiz_id,score) VALUES (1,1,0);
INSERT INTO results (user_id,quiz_id,score) VALUES (1,2,0);
INSERT INTO results (user_id,quiz_id,score) VALUES (1,3,0);

-- User 2
INSERT INTO results (user_id,quiz_id,score) VALUES (2,1,0);
INSERT INTO results (user_id,quiz_id,score) VALUES (2,2,0);
INSERT INTO results (user_id,quiz_id,score) VALUES (2,3,0);

disconnect;
exit;


