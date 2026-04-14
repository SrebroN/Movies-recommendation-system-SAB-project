package student;

import student.Connecting.DB;
import rs.ac.bg.etf.sab.operations.GeneralOperations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class sn200255_GeneralOperations implements GeneralOperations {
    private Connection conn= DB.getInstance().getConnection();
    @Override
    public void eraseAll() {
        try {
            conn.setAutoCommit(false);
            PreparedStatement ps=conn.prepareStatement("DELETE FROM WATCHLIST WHERE 1=1");
            ps.executeUpdate();
            ps=conn.prepareStatement("DELETE FROM RATINGS WHERE 1=1");
            ps.executeUpdate();
            ps=conn.prepareStatement("DELETE FROM MOVIE_TAGS WHERE 1=1");
            ps.executeUpdate();
            ps=conn.prepareStatement("DELETE FROM MOVIE_GENRES WHERE 1=1");
            ps.executeUpdate();
            ps=conn.prepareStatement("DELETE FROM TAG WHERE 1=1");
            ps.executeUpdate();
            ps=conn.prepareStatement("DELETE FROM [USER] WHERE 1=1");
            ps.executeUpdate();
            ps=conn.prepareStatement("DELETE FROM GENRE WHERE 1=1");
            ps.executeUpdate();
            ps=conn.prepareStatement("DELETE FROM MOVIE WHERE 1=1");
            ps.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
