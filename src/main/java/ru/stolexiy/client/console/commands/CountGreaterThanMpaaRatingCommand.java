package ru.stolexiy.client.console.commands;

import ru.stolexiy.client.console.IO;
import ru.stolexiy.data.MpaaRating;

public class CountGreaterThanMpaaRatingCommand extends AbstractCommand {

    @Override
    public MpaaRating execute(IO io) {
        return io.readEnum("MPAA рейтинг", MpaaRating.values(), MpaaRating.class, MpaaRating.G);
    }
}
