USE [sn200255]
GO

/****** Object:  Trigger [dbo].[TR_BLOCK_EXTREME_]    Script Date: 18.10.2025. 12:15:37 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE TRIGGER [dbo].[TR_BLOCK_EXTREME_] ON [dbo].[RATINGS] 
    FOR INSERT, UPDATE
AS 
BEGIN
    SET NOCOUNT ON;

    DECLARE @extreme int, @neutral int, @rating int, @idUsr int, @idMov int, @idGen int;

    DECLARE curInserted CURSOR FOR 
        SELECT idUse, IdMov, Rating FROM inserted;

    OPEN curInserted;
    FETCH NEXT FROM curInserted INTO @idUsr, @idMov, @rating;

    WHILE @@FETCH_STATUS = 0
    BEGIN
        DECLARE curGenres CURSOR FOR
            SELECT MG.IdGen FROM MOVIE_GENRES MG WHERE MG.IdMov = @idMov;

        OPEN curGenres;
        FETCH NEXT FROM curGenres INTO @idGen;

        WHILE @@FETCH_STATUS = 0
        BEGIN
            IF @rating IN (1, 10)
            BEGIN
                SET @extreme = (
                    SELECT COUNT(*) 
                    FROM MOVIE_GENRES MG
                    WHERE MG.IdGen = @idGen AND MG.IdMov IN (
                        SELECT R.IdMov
                        FROM RATINGS R
                        WHERE R.IdUse = @idUsr AND Rating IN (1,10)
                    )
                );

                SET @neutral = (
                    SELECT COUNT(*) 
                    FROM MOVIE_GENRES MG
                    WHERE MG.IdGen = @idGen AND MG.IdMov IN (
                        SELECT R.IdMov
                        FROM RATINGS R
                        WHERE R.IdUse = @idUsr AND Rating IN (6,7,8)
                    )
                );

                IF @extreme > 3 AND @neutral < 3
                BEGIN
                    CLOSE curGenres;
                    DEALLOCATE curGenres;
                    CLOSE curInserted;
                    DEALLOCATE curInserted;
                    RAISERROR('GRESKA: Extremne ocene nisu dozvoljene', 16, 1);
                    ROLLBACK TRANSACTION;
                    RETURN;
                END
            END

            FETCH NEXT FROM curGenres INTO @idGen;
        END

        CLOSE curGenres;
        DEALLOCATE curGenres;

        EXECUTE dbo.SP_REWARD_USER_ @idUsr=@idUsr, @IdMov = @idMov;

        FETCH NEXT FROM curInserted INTO @idUsr, @idMov, @rating;
    END

    CLOSE curInserted;
    DEALLOCATE curInserted;
END
GO

ALTER TABLE [dbo].[RATINGS] ENABLE TRIGGER [TR_BLOCK_EXTREME_]
GO

