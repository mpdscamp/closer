package com.labprog.closer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Nome do banco de dados
    private static final String DATABASE_NAME = "closer";
    // Versão do banco de dados
    private static final int DATABASE_VERSION = 1;
    // Nome da tabela
    private static final String TABLE_NAME = "log";
    // Colunas da tabela
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_EMAIL = "email";

    // Construtor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Chamado quando o banco de dados é criado pela primeira vez
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_EMAIL + " TEXT"
                + ")";
        db.execSQL(CREATE_TABLE);
    }

    // Chamado quando o banco de dados precisa ser atualizado
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Descarta a tabela antiga se existir
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        // Cria a tabela novamente
        onCreate(db);
    }

    // Método para adicionar um novo email ao banco de dados
    public void addEmail(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, email); // Colocando o email
        db.insert(TABLE_NAME, null, values); // Inserindo a linha
        db.close(); // Fechando a conexão com o banco de dados
    }

    // Método para buscar todos os emails
    public Cursor getAllEmails() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_NAME, new String[] { COLUMN_EMAIL }, null, null, null, null, COLUMN_ID + " DESC");
    }
}
