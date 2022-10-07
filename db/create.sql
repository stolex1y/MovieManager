CREATE TABLE Users
(
    login text PRIMARY KEY,
    pass  char(16) NOT NULL
);

CREATE TABLE Countries
(
    id   serial PRIMARY KEY,
    name text NOT NULL UNIQUE
);

CREATE TABLE MovieGenres
(
    id   serial PRIMARY KEY,
    name text NOT NULL UNIQUE
);

CREATE TABLE Persons
(
    id             serial PRIMARY KEY,
    name           text NOT NULL,
    growthInMetres numeric NULL,
    nationality    int NULL REFERENCES Countries (id),
    filmCount      int NULL
);

CREATE TABLE MpaaRatings
(
    id   serial PRIMARY KEY,
    name text NOT NULL UNIQUE
);

CREATE TABLE Movies
(
    id                serial PRIMARY KEY,
    owner             text      NOT NULL REFERENCES Users (login) ON UPDATE CASCADE ON DELETE CASCADE,
    name              text      NOT NULL,
    productionYear    int       NOT NULL,
    country           int NULL REFERENCES Countries (id) ON UPDATE CASCADE,
    genre             int NULL REFERENCES MovieGenres (id) ON UPDATE CASCADE,
    director          int       NOT NULL REFERENCES Persons (id) ON UPDATE CASCADE ON DELETE CASCADE,
    budget            bigint NULL,
    fees              bigint NULL,
    mpaaRating        int NULL REFERENCES MpaaRatings (id) ON UPDATE CASCADE,
    durationInMinutes int NULL,
    oscarsCount       int NULL,
    creationDate      timestamp NOT NULL DEFAULT current_timestamp
);