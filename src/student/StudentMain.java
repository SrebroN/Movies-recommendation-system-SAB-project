package student;

import rs.ac.bg.etf.sab.operations.*;
import rs.ac.bg.etf.sab.tests.TestHandler;
import rs.ac.bg.etf.sab.tests.TestRunner;

import java.util.List;

public class StudentMain {
    public static void printMovies(MoviesOperations moviesOperations) {
        List<Integer>ids;
        ids=moviesOperations.getAllMovieIds();
        System.out.print("Movie IDs: ");
        for(Integer id:ids){
            System.out.print(id+"  ");
        }
        System.out.println();
    }
    public static void main(String[] args) throws Exception {
// Uncomment and change fallowing lines
         GeneralOperations generalOperations = new sn200255_GeneralOperations();
        GenresOperations genresOperations = new sn200255_GenresOperations();
        MoviesOperations moviesOperations = new sn200255_MoviesOperations();
        RatingsOperations ratingsOperation = new sn200255_RatingsOperations();
        TagsOperations tagsOperations = new sn200255_TagsOperations();
        UsersOperations usersOperations = new sn200255_UsersOperations();
        WatchlistsOperations watchlistsOperations = new sn200255_WatchlistsOperations();


        TestHandler.createInstance(
                genresOperations,
                moviesOperations,
                ratingsOperation,
                tagsOperations,
                usersOperations,
                watchlistsOperations,
                generalOperations);
             TestRunner.runTests();
    }
}