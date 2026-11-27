INSERT INTO military_ranks (name) VALUES ('рядовой'), ('сержант'), ('лейтенант'), ('капитан');

INSERT INTO users (username, password, role)
VALUES ('admin', '$2a$10$APlOpA7jOWSfP5S2bbstle0KM.BC67J5NS6sSF7tRJncJxxXYxNmK', 'ROLE_ADMIN');
INSERT INTO users (username, password, role)
VALUES ('user', '$2a$10$GPw.Hh1sQtcnjGsmUc.iZONPuLyIg1o5XnVAYI3.qS.mD0hlTJqKi', 'ROLE_USER');

