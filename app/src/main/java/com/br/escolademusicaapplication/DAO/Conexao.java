package com.br.escolademusicaapplication.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.br.escolademusicaapplication.OBJETOS.Aluno;
import com.br.escolademusicaapplication.OBJETOS.Aluno_Professor;
import com.br.escolademusicaapplication.OBJETOS.Curso;
import com.br.escolademusicaapplication.OBJETOS.Galeria;
import com.br.escolademusicaapplication.OBJETOS.Professor;
import com.br.escolademusicaapplication.TelaLoginActivity;

import java.util.ArrayList;
import java.util.List;

public class Conexao extends SQLiteOpenHelper {
    private static Conexao instance; // Instância estática para armazenar a única instância da conexão
    private SQLiteDatabase db;

    // Construtor privado para impedir a criação de instâncias diretamente
    public Conexao(@Nullable Context context) {
        super(context, "escola_do_rock", null, 1);
        this.db = getWritableDatabase(); // Inicializa o objeto db
    }

    // Método estático para obter a instância única da conexão
    public static synchronized Conexao getInstance(Context context) {
        if (instance == null) {
            instance = new Conexao(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db; // Inicialização movida aqui
        String sql_aluno = "CREATE TABLE aluno (" +
                "aluno_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "aluno_nome TEXT ," +
                "aluno_cpf TEXT unique," +
                "aluno_senha TEXT , " +
                "endereco TEXT, " +
                "aluno_data_nasci TEXT," +
                "aluno_telefone TEXT," +
                "aluno_sexo TEXT," +
                "aluno_foto TEXT, aluno_status TEXT);";
        String sql_professor = "CREATE TABLE professor (" +
                "professor_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "professor_nome TEXT ," +
                "professor_cpf TEXT unique," +
                "professor_senha TEXT ," +
                "professor_disciplina TEXT," +
                "professor_telefone TEXT," +
                "professor_endeco TEXT," +
                "professor_sexo TEXT ," +
                "professor_foto TEXT, professor_status TEXT);";
        String sql_aluno_professor = "CREATE TABLE aluno_professor (" +
                "id_aluno INTEGER, " +
                "id_professor INTEGER," +
                "aluno_nota_primeiro_bi INTEGER," +
                "aluno_nota_segundo_bi INTEGER," +
                "aluno_faltas INTEGER," +
                "PRIMARY KEY (id_aluno, id_professor), " +
                "FOREIGN KEY (id_aluno) REFERENCES aluno(aluno_id) ON DELETE CASCADE, " +
                "FOREIGN KEY (id_professor) REFERENCES professor(professor_id) ON DELETE CASCADE);";
//abordamos o on delete cascade, para quando for excluir o professor, ele não quebrar, pois
// dentro do mundo sql, não se exclui um objeto que esteja vinculado a outro objeto, temos que
// excluir em cataca um e após o outro
        String sql_galeria_escola = "CREATE TABLE galeria (" +
                "galeria_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "galeria_titulo TEXT ," +
                "galeria_descricao TEXT unique," +
                "galeria_imagem TEXT);";
        String sql_curso_escola = "CREATE TABLE curso (" +
                "curso_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "curso_titulo TEXT ," +
                "curso_descricao TEXT unique," +
                "curso_imagem TEXT);";
        db.execSQL(sql_curso_escola);
        db.execSQL(sql_galeria_escola);
        db.execSQL(sql_aluno);
        db.execSQL(sql_professor);
        db.execSQL(sql_aluno_professor);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS aluno_professor");
        db.execSQL("DROP TABLE IF EXISTS aluno");
        db.execSQL("DROP TABLE IF EXISTS professor");
        db.execSQL("DROP TABLE IF EXISTS galeria");
        db.execSQL("DROP TABLE IF EXISTS curso");
        onCreate(db);
    }

    //método responsável pelo login do aluno, separando o login de admin, professor e aluno
    public String autenticarUsuario(String cpf, String senha, Context context) {
        if (db == null) {
            db = getWritableDatabase(); // Inicializa o objeto db se for nulo
        }
        String tipoUsuario = "";
        // Verificação para o usuário admin
        if (cpf.equals("995430914") && senha.equalsIgnoreCase("admin")) {
            Toast.makeText(context, "Admin autenticado", Toast.LENGTH_LONG).show();
            tipoUsuario = "admin"; // Retorna o tipo de usuário como admin
        } else {
            // Verificação para alunos
            String sql_busca_aluno = "SELECT aluno_nome, aluno_status FROM aluno WHERE aluno_cpf = ? AND aluno_senha = ?";
            Cursor cAluno = db.rawQuery(sql_busca_aluno, new String[]{cpf, senha});
            if (cAluno.moveToFirst()) {
                String statusAluno = cAluno.getString(cAluno.getColumnIndexOrThrow("aluno_status"));
                if (statusAluno.equals("ativo")) {
                    Toast.makeText(context, "Aluno autenticado", Toast.LENGTH_LONG).show();
                    tipoUsuario = "aluno"; // Retorna o tipo de usuário como aluno
                } else {
                    Toast.makeText(context, "Seu cadastro está inativo. Favor entrar em contato com o administrador.", Toast.LENGTH_LONG).show();
                }
            } else {
                // Verificação para professores
                String sql_busca_professor = "SELECT professor_nome FROM professor WHERE professor_cpf = ? AND professor_senha = ?";
                Cursor cProfessor = db.rawQuery(sql_busca_professor, new String[]{cpf, senha});
                if (cProfessor.moveToFirst()) {
                    Toast.makeText(context, "Professor autenticado", Toast.LENGTH_LONG).show();
                    tipoUsuario = "professor"; // Retorna o tipo de usuário como professor
                } else {
                    // Se nenhum dos usuários for autenticado, mostrar mensagem de erro
                    Toast.makeText(context, "Usuário não autenticado", Toast.LENGTH_LONG).show();
                    // Aqui podemos lançar uma exceção ou retornar um valor específico
                    // Vou retornar uma string vazia para indicar falha na autenticação
                    tipoUsuario = "";
                }
            }
        }
        return tipoUsuario;
    }

    public int insereAluno(Aluno aluno) {
        // Inicializa o objeto db se for nulo
        if (db == null) {
            db = getWritableDatabase();
        }

        try {
            // Mostra para o banco que será inserido dados.
            ContentValues dados_aluno = new ContentValues(); // É o objeto que armazena valores
            // Dados que serão salvos no banco
            dados_aluno.put("aluno_nome", aluno.getAluno_nome());
            dados_aluno.put("aluno_senha", aluno.getAluno_senha());
            dados_aluno.put("aluno_cpf", aluno.getAluno_cpf());
            dados_aluno.put("aluno_telefone", aluno.getAluno_telefone());
            dados_aluno.put("aluno_status", aluno.getStatus());

            // Inserção do aluno no banco e obtenção do ID do aluno recém-cadastrado
            long idAluno = db.insertOrThrow("aluno", null, dados_aluno);

            // Busca o professor da matéria virtual pelo ID
            Professor professorVirtual = buscarProfessorPorId("1"); // O ID do professor da matéria virtual

            if (professorVirtual != null) {
                // Buscar o aluno recém-cadastrado pelo ID
                Aluno alunoCadastrado = buscarAlunoPorId(String.valueOf(idAluno));

                // Vincular o aluno ao professor da matéria virtual
                if (alunoCadastrado != null) {
                    boolean sucesso = salvarAlunoProfessor(alunoCadastrado, professorVirtual);
                    if (!sucesso) {
                        // Se houver um erro ao vincular o aluno ao professor, imprime uma mensagem de erro ou faz o tratamento necessário
                        Log.e("Conexao", "Erro ao vincular aluno ao professor da matéria virtual");
                    }
                } else {
                    // Se o aluno recém-cadastrado não for encontrado, imprime uma mensagem de erro ou faz o tratamento necessário
                    Log.e("Conexao", "Aluno recém-cadastrado não encontrado");
                }
            } else {
                // Se o professor da matéria virtual não for encontrado, imprime uma mensagem de erro ou faz o tratamento necessário
                Log.e("Conexao", "Professor da matéria virtual não encontrado");
            }

            // Retorna o ID do aluno recém-cadastrado
            return (int) idAluno;

        } catch (SQLiteConstraintException erro) {
            // Em caso de exceção de violação de restrição de chave estrangeira, retorna -1
            Log.e("Conexao", "Erro ao inserir aluno: " + erro.getMessage());
            return -1;
        } catch (Exception e) {
            // Em caso de exceção geral, imprime o stack trace e retorna -1
            e.printStackTrace();
            return -1;
        }
    }

    public boolean insereProfessor(Professor professor) {
        if (db == null) {
            db = getWritableDatabase(); // Inicializa o objeto db se for nulo
        }
        try {
            //mostrando para o banco que será inserido dados.
            ContentValues dados_professor = new ContentValues();//é o objeto que armazena valores
            //dados que seram salvos no banco
            dados_professor.put("professor_nome", professor.getProfessor_nome());
            dados_professor.put("professor_senha", professor.getProfessor_senha());
            dados_professor.put("professor_cpf", professor.getProfessor_cpf());
            dados_professor.put("professor_disciplina", professor.getProfessor_disciplina());
            dados_professor.put("professor_foto", professor.getProfessor_foto());
            dados_professor.put("professor_telefone", professor.getProfessor_telefone());
            //-----------------------------------------------------------
            db.insertOrThrow("professor", null, dados_professor);
        } catch (SQLiteConstraintException erro) {
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    //método responsável por pegar o nome do aluno logado, para que na seção mostre quem esta logado
    public String buscarNomeUsuario(String cpf, String senha, Context context) {
        String nomeUsuario = null;
        // Verificação para alunos
        String sql_busca_aluno = "SELECT aluno_nome FROM aluno WHERE aluno_cpf = ? AND aluno_senha = ?";
        Cursor cAluno = db.rawQuery(sql_busca_aluno, new String[]{cpf, senha});
        if (cAluno.moveToFirst()) {
            nomeUsuario = cAluno.getString(cAluno.getColumnIndexOrThrow("aluno_nome"));
            return nomeUsuario; // Retorna o nome do aluno
        }
        // Verificação para professores
        String sql_busca_professor = "SELECT professor_nome FROM professor WHERE professor_cpf = ? AND professor_senha = ?";
        Cursor cProfessor = db.rawQuery(sql_busca_professor, new String[]{cpf, senha});
        if (cProfessor.moveToFirst()) {
            nomeUsuario = cProfessor.getString(cProfessor.getColumnIndexOrThrow("professor_nome"));
            return nomeUsuario; // Retorna o nome do professor
        }
        return nomeUsuario; // Retorna null se nenhum usuário for encontrado
    }

    public List<Professor> buscarTodosProfessores() {
        String sql = "SELECT * FROM professor ORDER BY professor_nome ASC";
        if (db == null || !db.isOpen()) {
            db = this.getReadableDatabase(); // Inicializa o banco de dados se estiver nulo
        }
        Cursor cursor = db.rawQuery(sql, null);
        List<Professor> professors = new ArrayList<>();
        while (cursor.moveToNext()) {
            Professor professor = new Professor();
            professor.setProfessor_id(cursor.getInt(cursor.getColumnIndexOrThrow("professor_id"))); // Configurando o ID
            professor.setProfessor_nome(cursor.getString(cursor.getColumnIndexOrThrow("professor_nome")));
            professor.setProfessor_disciplina(cursor.getString(cursor.getColumnIndexOrThrow("professor_disciplina")));
            professor.setPorfessor_sexo(cursor.getString(cursor.getColumnIndexOrThrow("professor_sexo")));
            professor.setProfessor_foto(cursor.getString(cursor.getColumnIndexOrThrow("professor_foto")));
            professor.setProfessor_telefone(cursor.getString(cursor.getColumnIndexOrThrow("professor_telefone")));
            professors.add(professor);
        }
        return professors;
    }

    private void excluirProfessor(int professorId) {
        String[] whereArgs = {String.valueOf(professorId)};
        int linhasAfetadas = db.delete("professor", "professor_id = ?", whereArgs);
        if (linhasAfetadas > 0) {
            Log.d("resultado", "excluirProfessor: deu certo");
        } else {
            Log.d("resultado", "excluirProfessor: nao deu certo");
        }
    }

    public boolean atualizarProfessor(Professor professor) {
        if (db == null) {
            db = getWritableDatabase(); // Inicializa o objeto db se for nulo
        }
        try {
            ContentValues valores = new ContentValues();
            valores.put("professor_nome", professor.getProfessor_nome());
            valores.put("professor_disciplina", professor.getProfessor_disciplina());

            if (!professor.getProfessor_foto().equals("")) {
                valores.put("professor_foto", professor.getProfessor_foto());
            }
            String[] whereArgs = {String.valueOf(professor.getProfessor_id())};
            int linhasAfetadas = db.update("professor", valores, "professor_id = ?", whereArgs);
            return linhasAfetadas > 0; // Retorna true se a atualização for bem-sucedida
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Retorna false se ocorrer algum erro
        }
    }

    public boolean atualizarAluno(Aluno aluno) {
        if (db == null) {
            db = getWritableDatabase(); // Inicializa o objeto db se for nulo
        }
        try {
            ContentValues valores = new ContentValues();
            valores.put("aluno_nome", aluno.getAluno_nome());
            if (!aluno.getAluno_foto().equals("")) {
                valores.put("aluno_foto", aluno.getAluno_foto());
            }
            String[] whereArgs = {String.valueOf(aluno.getAluno_id())};
            int linhasAfetadas = db.update("aluno", valores, "aluno_id= ?", whereArgs);
            return linhasAfetadas > 0; // Retorna true se a atualização for bem-sucedida
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Retorna false se ocorrer algum erro
        }
    }

    public Aluno buscarAluno(String cpf, String senha, Context context) {
        if (db == null) {
            db = getWritableDatabase(); // Inicializa o objeto db se for nulo
        }
        Aluno alunoAutenticado = null;
        // Consulta para buscar o aluno pelo CPF e senha
        String sql_busca_aluno = "SELECT * FROM aluno WHERE aluno_cpf = ? AND aluno_senha = ?";
        Cursor cursor = db.rawQuery(sql_busca_aluno, new String[]{cpf, senha});
        // Verifica se o cursor possui resultados
        if (cursor.moveToFirst()) {
            alunoAutenticado = new Aluno();
            alunoAutenticado.setAluno_id(cursor.getInt(cursor.getColumnIndexOrThrow("aluno_id")));
            alunoAutenticado.setAluno_nome(cursor.getString(cursor.getColumnIndexOrThrow("aluno_nome")));
            alunoAutenticado.setAluno_sexo(cursor.getString(cursor.getColumnIndexOrThrow("aluno_sexo")));
        }
        return alunoAutenticado;
    }

    public Professor buscarProfessor(String cpf, String senha, Context context) {
        if (db == null) {
            db = getWritableDatabase(); // Inicializa o objeto db se for nulo
        }
        Professor professorAutenticado = null;
        // Consulta para buscar o aluno pelo CPF e senha
        String sql_busca_professor = "SELECT * FROM professor WHERE professor_cpf = ? AND professor_senha = ?";
        Cursor cursor = db.rawQuery(sql_busca_professor, new String[]{cpf, senha});
        if (cursor.moveToFirst()) {
            professorAutenticado = new Professor();
            professorAutenticado.setProfessor_id(cursor.getInt(cursor.getColumnIndexOrThrow("professor_id")));
            professorAutenticado.setProfessor_nome(cursor.getString(cursor.getColumnIndexOrThrow("professor_nome")));
            professorAutenticado.setProfessor_cpf(cursor.getString(cursor.getColumnIndexOrThrow("professor_cpf")));
            professorAutenticado.setProfessor_disciplina(cursor.getString(cursor.getColumnIndexOrThrow("professor_disciplina")));
            professorAutenticado.setProfessor_foto(cursor.getString(cursor.getColumnIndexOrThrow("professor_foto")));
        }
        return professorAutenticado;
    }

    public List<Aluno> buscarTodosAlunos() {
        if (db == null) {
            db = getWritableDatabase(); // Inicializa o objeto db se for nulo
        }
        List<Aluno> listaAlunos = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM aluno ORDER BY aluno_nome ASC", null);
        if (cursor.moveToFirst()) {
            do {
                Aluno aluno = new Aluno();
                aluno.setAluno_id(cursor.getInt(cursor.getColumnIndexOrThrow("aluno_id")));
                aluno.setAluno_nome(cursor.getString(cursor.getColumnIndexOrThrow("aluno_nome")));
                aluno.setAluno_foto(cursor.getString(cursor.getColumnIndexOrThrow("aluno_foto")));
                aluno.setAluno_cpf(cursor.getString(cursor.getColumnIndexOrThrow("aluno_cpf"))); // Adicione essa linha para definir o CPF
                aluno.setAluno_telefone(cursor.getString(cursor.getColumnIndexOrThrow("aluno_telefone"))); // Adicione essa linha para definir o telefone
                aluno.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("aluno_status")));
                aluno.setAluno_senha(cursor.getString(cursor.getColumnIndexOrThrow("aluno_senha")));
                listaAlunos.add(aluno);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return listaAlunos;
    }

    public String buscarFotoAluno(int alunoId) {
        if (db == null) {
            db = getWritableDatabase(); // Inicializa o objeto db se for nulo
        }
        String fotoAluno = null;
        String[] projection = {"aluno_foto"};
        String selection = "aluno_id = ?";
        String[] selectionArgs = {String.valueOf(alunoId)};
        Cursor cursor = db.query("aluno", projection, selection, selectionArgs,
                null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            fotoAluno = cursor.getString(cursor.getColumnIndexOrThrow("aluno_foto"));
            cursor.close();
        }
        return fotoAluno;
    }

    public Aluno buscarAlunoPorId(String idAluno) {
        if (db == null) {
            db = getWritableDatabase(); // Inicializa o objeto db se for nulo
        }
        Aluno aluno = null;
        String query = "SELECT * FROM aluno WHERE aluno_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{idAluno});
        if (cursor.moveToFirst()) {
            aluno = new Aluno();
            aluno.setAluno_id(cursor.getInt(cursor.getColumnIndexOrThrow("aluno_id")));
            aluno.setAluno_nome(cursor.getString(cursor.getColumnIndexOrThrow("aluno_nome")));
            aluno.setAluno_foto(cursor.getString(cursor.getColumnIndexOrThrow("aluno_foto")));
            aluno.setAluno_telefone(cursor.getString(cursor.getColumnIndexOrThrow("aluno_telefone")));
            aluno.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("aluno_status"))); // Adicione esta linha
        }
        cursor.close(); // Feche o cursor
        return aluno;
    }


    public Professor buscarProfessorPorId(String idProfessor) {
        if (db == null) {
            db = getWritableDatabase(); // Inicializa o objeto db se for nulo
        }
        Professor professor = null;
        String query = "SELECT * FROM professor WHERE professor_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{idProfessor});
        if (cursor.moveToFirst()) {
            professor = new Professor();
            professor.setProfessor_id(cursor.getInt(cursor.getColumnIndexOrThrow("professor_id")));
            professor.setProfessor_nome(cursor.getString(cursor.getColumnIndexOrThrow("professor_nome")));
            professor.setProfessor_foto(cursor.getString(cursor.getColumnIndexOrThrow("professor_foto")));
        }
        return professor;
    }

    public boolean salvarAlunoProfessor(Aluno aluno, Professor professor) {
        if (db == null) {
            db = getWritableDatabase(); // Inicializa o objeto db se for nulo
        }
        ContentValues values = new ContentValues();
        values.put("id_aluno", aluno.getAluno_id());
        values.put("id_professor", professor.getProfessor_id());
        values.put("aluno_nota_primeiro_bi", 0); // Definindo as notas como zero
        values.put("aluno_nota_segundo_bi", 0);
        values.put("aluno_faltas", 0); // Definindo as faltas como zero
        long resultado = db.insert("aluno_professor", null, values);
        return resultado != -1; // Retorna true se a inserção for bem-sucedida
    }

    public void listarAlunosDoAluno(int idAluno) {
        if (db == null) {
            db = getWritableDatabase(); // Inicializa o objeto db se for nulo
        }
        String sql = "SELECT ap.id_aluno, ap.id_professor, p.professor_disciplina, ap.aluno_nota_primeiro_bi, " +
                "ap.aluno_nota_segundo_bi, ap.aluno_faltas, a.aluno_nome AS nome_aluno, " +
                "p.professor_nome AS nome_professor " +
                "FROM aluno_professor ap " +
                "INNER JOIN aluno a ON ap.id_aluno = a.aluno_id " +
                "INNER JOIN professor p ON ap.id_professor = p.professor_id " +
                "WHERE ap.id_aluno = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(idAluno)});
        if (cursor.moveToFirst()) {
            do {
                // Obtenha os dados de cada registro
                int idProfessor = cursor.getInt(cursor.getColumnIndexOrThrow("id_professor"));
                String disciplina = cursor.getString(cursor.getColumnIndexOrThrow("professor_disciplina"));
                int notaPrimeiroBim = cursor.getInt(cursor.getColumnIndexOrThrow("aluno_nota_primeiro_bi"));
                int notaSegundoBim = cursor.getInt(cursor.getColumnIndexOrThrow("aluno_nota_segundo_bi"));
                int faltas = cursor.getInt(cursor.getColumnIndexOrThrow("aluno_faltas"));
                String nomeAluno = cursor.getString(cursor.getColumnIndexOrThrow("nome_aluno"));
                String nomeProfessor = cursor.getString(cursor.getColumnIndexOrThrow("nome_professor"));
                // Exiba os dados no console ou prompt
                System.out.println("Nome do Aluno: " + nomeAluno);
                System.out.println("Nome do Professor: " + nomeProfessor);
                System.out.println("Disciplina: " + disciplina);
                System.out.println("Nota do Primeiro Bimestre: " + notaPrimeiroBim);
                System.out.println("Nota do Segundo Bimestre: " + notaSegundoBim);
                System.out.println("Faltas: " + faltas);
                System.out.println("---------------------------------------");
            } while (cursor.moveToNext());
        }
    }

    public List<Aluno_Professor> listarAlunosDoAlunoTelaPortal(int idAluno) {
        if (db == null) {
            db = getWritableDatabase(); // Inicializa o objeto db se for nulo
        }

        String sql = "SELECT p.professor_nome, p.professor_disciplina, p.professor_telefone, " +
                "ap.aluno_nota_primeiro_bi, ap.aluno_nota_segundo_bi, ap.aluno_faltas " +
                "FROM aluno_professor ap " +
                "INNER JOIN aluno a ON ap.id_aluno = a.aluno_id " +
                "INNER JOIN professor p ON ap.id_professor = p.professor_id " +
                "WHERE ap.id_aluno = ?";

        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(idAluno)});
        List<Aluno_Professor> listaAlunosProfessor = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                // Obtenha os dados de cada registro
                String nomeProfessor = cursor.getString(cursor.getColumnIndexOrThrow("professor_nome"));
                String disciplina = cursor.getString(cursor.getColumnIndexOrThrow("professor_disciplina"));
                String telefoneProfessor = cursor.getString(cursor.getColumnIndexOrThrow("professor_telefone"));
                int notaPrimeiroBim = cursor.getInt(cursor.getColumnIndexOrThrow("aluno_nota_primeiro_bi"));
                int notaSegundoBim = cursor.getInt(cursor.getColumnIndexOrThrow("aluno_nota_segundo_bi"));
                int faltas = cursor.getInt(cursor.getColumnIndexOrThrow("aluno_faltas"));

                // Crie um objeto Aluno_Professor com os dados obtidos
                Aluno_Professor alunoProfessor = new Aluno_Professor();
                Professor professor = new Professor();
                professor.setProfessor_nome(nomeProfessor);
                professor.setProfessor_disciplina(disciplina);
                professor.setProfessor_telefone(telefoneProfessor); // Definir o número de telefone do professor
                alunoProfessor.setProfessor(professor);
                alunoProfessor.setAluno_notaPrimeiroBim(notaPrimeiroBim);
                alunoProfessor.setAluno_notaSegundoBim(notaSegundoBim);
                alunoProfessor.setAluno_faltas(faltas);

                // Adicione o objeto à lista
                listaAlunosProfessor.add(alunoProfessor);
            } while (cursor.moveToNext());
        }

        cursor.close(); // Fechar o cursor após usar

        return listaAlunosProfessor;
    }


    public List<Aluno_Professor> buscarAlunosDoProfessor(int idProfessor) {
        if (db == null) {
            db = getWritableDatabase(); // Inicializa o objeto db se for nulo
        }
        String sql = "SELECT a.aluno_id, a.aluno_nome, ap.aluno_nota_primeiro_bi, ap.aluno_nota_segundo_bi, ap.aluno_faltas " +
                "FROM aluno_professor ap " +
                "INNER JOIN aluno a ON ap.id_aluno = a.aluno_id " +
                "WHERE ap.id_professor = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(idProfessor)});
        List<Aluno_Professor> listaAlunosProfessor = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                // Obtenha os dados de cada registro
                int alunoId = cursor.getInt(cursor.getColumnIndexOrThrow("aluno_id"));
                String alunoNome = cursor.getString(cursor.getColumnIndexOrThrow("aluno_nome"));
                int notaPrimeiroBim = cursor.getInt(cursor.getColumnIndexOrThrow("aluno_nota_primeiro_bi"));
                int notaSegundoBim = cursor.getInt(cursor.getColumnIndexOrThrow("aluno_nota_segundo_bi"));
                int faltas = cursor.getInt(cursor.getColumnIndexOrThrow("aluno_faltas"));
                // Crie objetos Aluno e Professor
                Aluno aluno = new Aluno();
                aluno.setAluno_id(alunoId);
                aluno.setAluno_nome(alunoNome);

                Professor professor = new Professor();
                professor.setProfessor_id(idProfessor);
                // Crie um objeto Aluno_Professor com os dados obtidos
                Aluno_Professor alunoProfessor = new Aluno_Professor();
                alunoProfessor.setAluno(aluno);
                alunoProfessor.setProfessor(professor);
                alunoProfessor.setAluno_notaPrimeiroBim(notaPrimeiroBim);
                alunoProfessor.setAluno_notaSegundoBim(notaSegundoBim);
                alunoProfessor.setAluno_faltas(faltas);
                // Adicione o objeto à lista
                listaAlunosProfessor.add(alunoProfessor);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return listaAlunosProfessor;
    }

    public boolean atualizarNotasFaltasAluno(Aluno_Professor alunoProfessor) {
        if (db == null) {
            db = getWritableDatabase(); // Inicializa o objeto db se for nulo
        }
        try {
            // Atualizar as notas e faltas do aluno
            ContentValues valores = new ContentValues();
            valores.put("aluno_nota_primeiro_bi", alunoProfessor.getAluno_notaPrimeiroBim());
            valores.put("aluno_nota_segundo_bi", alunoProfessor.getAluno_notaSegundoBim());
            valores.put("aluno_faltas", alunoProfessor.getAluno_faltas());
            String[] whereArgs = {String.valueOf(alunoProfessor.getAluno().getAluno_id()),
                    String.valueOf(alunoProfessor.getProfessor().getProfessor_id())};
            int linhasAfetadas = db.update("aluno_professor", valores,
                    "id_aluno = ? AND id_professor = ?", whereArgs);
            // Verificar se a atualização foi bem-sucedida
            return linhasAfetadas > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Retorna false se ocorrer algum erro
        }
    }

    // Método para atualizar as notas e faltas do aluno no banco de dados
    public boolean atualizarNotasFaltasAluno(int alunoId, int idProfessor, int novaNotaPrimeiroBim, int novaNotaSegundoBim, int novasFaltas) {
        if (db == null) {
            db = getWritableDatabase(); // Inicializa o objeto db se for nulo
        }
        // Atualize as notas e faltas do aluno no banco de dados
        ContentValues values = new ContentValues();
        values.put("aluno_nota_primeiro_bi", novaNotaPrimeiroBim);
        values.put("aluno_nota_segundo_bi", novaNotaSegundoBim);
        values.put("aluno_faltas", novasFaltas);
        // Realize a atualização no banco de dados e obtenha o número de linhas afetadas
        int linhasAfetadas = db.update("aluno_professor", values, "id_aluno = ? AND id_professor = ?", new String[]{String.valueOf(alunoId), String.valueOf(idProfessor)});
        // Verifica se a atualização foi bem-sucedida (se uma ou mais linhas foram afetadas)
        return linhasAfetadas > 0;
    }

    public List<Aluno_Professor> listarAlunosPorNota() {
        if (db == null) {
            db = getWritableDatabase(); // Inicializa o objeto db se for nulo
        }
        List<Aluno_Professor> listaAlunosProfessor = new ArrayList<>();
        // Consulta SQL para selecionar todos os registros da tabela aluno_professor ordenados pela soma das notas em ordem decrescente,
        // incluindo os nomes dos alunos e professores
        String sql = "SELECT a.aluno_id, a.aluno_nome, p.professor_id, p.professor_nome, ap.aluno_nota_primeiro_bi, ap.aluno_nota_segundo_bi, " +
                "((ap.aluno_nota_primeiro_bi + ap.aluno_nota_segundo_bi) / 2) AS nota_total  " +
                "FROM aluno_professor ap " +
                "INNER JOIN aluno a ON ap.id_aluno = a.aluno_id " +
                "INNER JOIN professor p ON ap.id_professor = p.professor_id " +
                "ORDER BY nota_total DESC " +
                "LIMIT 5"; //
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                // Obtenha os dados de cada registro
                int alunoId = cursor.getInt(cursor.getColumnIndexOrThrow("aluno_id"));
                String alunoNome = cursor.getString(cursor.getColumnIndexOrThrow("aluno_nome"));
                int professorId = cursor.getInt(cursor.getColumnIndexOrThrow("professor_id"));
                String professorNome = cursor.getString(cursor.getColumnIndexOrThrow("professor_nome"));
                int notaPrimeiroBim = cursor.getInt(cursor.getColumnIndexOrThrow("aluno_nota_primeiro_bi"));
                int notaSegundoBim = cursor.getInt(cursor.getColumnIndexOrThrow("aluno_nota_segundo_bi"));
                // Crie objetos Aluno e Professor
                Aluno aluno = new Aluno();
                aluno.setAluno_id(alunoId);
                aluno.setAluno_nome(alunoNome);

                Professor professor = new Professor();
                professor.setProfessor_id(professorId);
                professor.setProfessor_nome(professorNome);
                // Crie um objeto Aluno_Professor com os dados obtidos
                Aluno_Professor alunoProfessor = new Aluno_Professor();
                alunoProfessor.setAluno(aluno);
                alunoProfessor.setProfessor(professor);
                alunoProfessor.setAluno_notaPrimeiroBim(notaPrimeiroBim);
                alunoProfessor.setAluno_notaSegundoBim(notaSegundoBim);
                // Adicione o objeto à lista
                listaAlunosProfessor.add(alunoProfessor);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return listaAlunosProfessor;
    }

    public boolean atualizarStatusAluno(int alunoId, String novoStatus) {
        if (db == null) {
            db = getWritableDatabase(); // Inicializa o objeto db se for nulo
        }
        try {
            ContentValues valores = new ContentValues();
            valores.put("aluno_status", novoStatus); // Atualizar apenas o status do aluno
            String[] whereArgs = {String.valueOf(alunoId)};
            int linhasAfetadas = db.update("aluno", valores, "aluno_id = ?", whereArgs);
            return linhasAfetadas > 0; // Retorna true se a atualização for bem-sucedida
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Retorna false se ocorrer algum erro
        }
    }

    // Método para buscar um aluno pelo CPF
    public Aluno buscarAlunoPorCPF(String cpfAluno) {
        if (db == null) {
            db = getWritableDatabase(); // Inicializa o objeto db se for nulo
        }
        Aluno aluno = null;
        String query = "SELECT * FROM aluno WHERE aluno_cpf = ?";
        Cursor cursor = db.rawQuery(query, new String[]{cpfAluno});
        if (cursor.moveToFirst()) {
            aluno = new Aluno();
            aluno.setAluno_id(cursor.getInt(cursor.getColumnIndexOrThrow("aluno_id")));
            aluno.setAluno_nome(cursor.getString(cursor.getColumnIndexOrThrow("aluno_nome")));
            aluno.setAluno_foto(cursor.getString(cursor.getColumnIndexOrThrow("aluno_foto")));
            aluno.setAluno_telefone(cursor.getString(cursor.getColumnIndexOrThrow("aluno_telefone")));
            aluno.setAluno_cpf(cursor.getString(cursor.getColumnIndexOrThrow("aluno_cpf")));
        }
        cursor.close(); // Importante fechar o cursor quando terminar de usá-lo
        return aluno;
    }

    // Método para atualizar a senha do aluno
    public void atualizarSenhaAluno(String cpfAluno, String novaSenha) {
        if (db == null) {
            db = getWritableDatabase(); // Inicializa o objeto db se for nulo
        }
        ContentValues values = new ContentValues();
        values.put("aluno_senha", novaSenha);
        String whereClause = "aluno_cpf = ?";
        String[] whereArgs = {cpfAluno};
        int rowsAffected = db.update("aluno", values, whereClause, whereArgs);
        Log.d("DB_UPDATE", "Rows affected: " + rowsAffected);
    }

    public boolean existeProfessorComCPF(String cpfProfessor) {
        if (db == null) {
            db = getWritableDatabase(); // Inicializa o objeto db se for nulo
        }

        String query = "SELECT COUNT(*) FROM professor WHERE professor_cpf = ?";
        try (Cursor cursor = db.rawQuery(query, new String[]{cpfProfessor})) {
            if (cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                return count > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean existeAlunoComCPF(String cpfAluno) {
        if (db == null) {
            db = getWritableDatabase(); // Inicializa o objeto db se for nulo
        }

        String query = "SELECT COUNT(*) FROM aluno WHERE aluno_cpf = ?";
        try (Cursor cursor = db.rawQuery(query, new String[]{cpfAluno})) {
            if (cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                return count > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Aluno_Professor> listarAlunosPorNotaSegundoBimestre() {
        if (db == null) {
            db = getWritableDatabase(); // Inicializa o objeto db se for nulo
        }
        List<Aluno_Professor> listaAlunosProfessor = new ArrayList<>();
        // Consulta SQL para selecionar os registros da tabela aluno_professor ordenados pela nota do segundo bimestre em ordem decrescente,
        // incluindo os nomes dos alunos e professores
        String sql = "SELECT a.aluno_id, a.aluno_nome, p.professor_id, p.professor_nome, ap.aluno_nota_segundo_bi " +
                "FROM aluno_professor ap " +
                "INNER JOIN aluno a ON ap.id_aluno = a.aluno_id " +
                "INNER JOIN professor p ON ap.id_professor = p.professor_id " +
                "ORDER BY ap.aluno_nota_segundo_bi DESC " +
                "LIMIT 5"; //
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                // Obtenha os dados de cada registro
                int alunoId = cursor.getInt(cursor.getColumnIndexOrThrow("aluno_id"));
                String alunoNome = cursor.getString(cursor.getColumnIndexOrThrow("aluno_nome"));
                int professorId = cursor.getInt(cursor.getColumnIndexOrThrow("professor_id"));
                String professorNome = cursor.getString(cursor.getColumnIndexOrThrow("professor_nome"));
                int notaSegundoBim = cursor.getInt(cursor.getColumnIndexOrThrow("aluno_nota_segundo_bi"));
                // Crie objetos Aluno e Professor
                Aluno aluno = new Aluno();
                aluno.setAluno_id(alunoId);
                aluno.setAluno_nome(alunoNome);

                Professor professor = new Professor();
                professor.setProfessor_id(professorId);
                professor.setProfessor_nome(professorNome);
                // Crie um objeto Aluno_Professor com os dados obtidos
                Aluno_Professor alunoProfessor = new Aluno_Professor();
                alunoProfessor.setAluno(aluno);
                alunoProfessor.setProfessor(professor);
                alunoProfessor.setAluno_notaSegundoBim(notaSegundoBim);
                // Adicione o objeto à lista
                listaAlunosProfessor.add(alunoProfessor);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return listaAlunosProfessor;
    }

    public List<Aluno_Professor> listarAlunosPorNotaPrimeiroBimestre() {
        if (db == null) {
            db = getWritableDatabase(); // Inicializa o objeto db se for nulo
        }
        List<Aluno_Professor> listaAlunosProfessor = new ArrayList<>();
        // Consulta SQL para selecionar os registros da tabela aluno_professor ordenados pela nota do primeiro bimestre em ordem decrescente,
        // incluindo os nomes dos alunos e professores
        String sql = "SELECT a.aluno_id, a.aluno_nome, p.professor_id, p.professor_nome, ap.aluno_nota_primeiro_bi " +
                "FROM aluno_professor ap " +
                "INNER JOIN aluno a ON ap.id_aluno = a.aluno_id " +
                "INNER JOIN professor p ON ap.id_professor = p.professor_id " +
                "ORDER BY ap.aluno_nota_primeiro_bi DESC " +
                "LIMIT 5"; //
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                // Obtenha os dados de cada registro
                int alunoId = cursor.getInt(cursor.getColumnIndexOrThrow("aluno_id"));
                String alunoNome = cursor.getString(cursor.getColumnIndexOrThrow("aluno_nome"));
                int professorId = cursor.getInt(cursor.getColumnIndexOrThrow("professor_id"));
                String professorNome = cursor.getString(cursor.getColumnIndexOrThrow("professor_nome"));
                int notaPrimeiroBim = cursor.getInt(cursor.getColumnIndexOrThrow("aluno_nota_primeiro_bi"));
                // Crie objetos Aluno e Professor
                Aluno aluno = new Aluno();
                aluno.setAluno_id(alunoId);
                aluno.setAluno_nome(alunoNome);

                Professor professor = new Professor();
                professor.setProfessor_id(professorId);
                professor.setProfessor_nome(professorNome);
                // Crie um objeto Aluno_Professor com os dados obtidos
                Aluno_Professor alunoProfessor = new Aluno_Professor();
                alunoProfessor.setAluno(aluno);
                alunoProfessor.setProfessor(professor);
                alunoProfessor.setAluno_notaPrimeiroBim(notaPrimeiroBim);
                // Adicione o objeto à lista
                listaAlunosProfessor.add(alunoProfessor);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return listaAlunosProfessor;
    }

    public boolean insertCurso(String titulo, String descricao, String imagemBase64) {
        SQLiteDatabase db = null;
        try {
            // Verifica se os parâmetros são válidos
            if (titulo == null || titulo.isEmpty() || descricao == null || descricao.isEmpty() || imagemBase64 == null) {
                Log.e("Conexao", "Parâmetros inválidos para inserção de curso.");
                return false;
            }

            db = getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put("curso_titulo", titulo);
            contentValues.put("curso_descricao", descricao);
            contentValues.put("curso_imagem", imagemBase64);

            // Inserção do curso no banco e obtenção do ID do curso recém-cadastrado
            long idCurso = db.insertOrThrow("curso", null, contentValues);

            if (idCurso != -1) {
                // Log quando a inserção é bem-sucedida
                Log.d("Conexao", "Curso inserido com sucesso. ID do curso: " + idCurso);
            }

            // Retorna true se a inserção foi bem-sucedida
            return idCurso != -1;

        } catch (SQLiteConstraintException e) {
            Log.e("Conexao", "Erro de constraint ao inserir curso: " + e.getMessage());
            return false;
        } catch (SQLiteException e) {
            Log.e("Conexao", "Erro ao inserir curso no banco de dados: " + e.getMessage());
            return false;
        } catch (Exception e) {
            Log.e("Conexao", "Erro inesperado ao inserir curso: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (db != null && db.isOpen()) {
                db.close(); // Fecha a conexão com o banco de dados
            }
        }
    }



    public List<Curso> getAllCursos() {
        List<Curso> listaCursos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query("curso", null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("curso_id"));
                String titulo = cursor.getString(cursor.getColumnIndexOrThrow("curso_titulo"));
                String descricao = cursor.getString(cursor.getColumnIndexOrThrow("curso_descricao"));
                String imagem = cursor.getString(cursor.getColumnIndexOrThrow("curso_imagem"));

                Log.d("CursoAdapter", "Curso encontrado: " + titulo); // Adicione este log para verificar se os dados estão corretos

                Curso curso = new Curso(id, titulo, descricao, imagem);
                listaCursos.add(curso);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return listaCursos;
    }

    public boolean updateCurso(int id, String titulo, String descricao, String imagem) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("curso_titulo", titulo);
        values.put("curso_descricao", descricao);

        // Verificar se a imagem foi alterada
        if (imagem != null && !imagem.isEmpty()) {
            values.put("curso_imagem", imagem);
        }

        int rowsAffected = db.update("curso", values, "curso_id=?", new String[]{String.valueOf(id)});
        db.close();
        return rowsAffected > 0;
    }


    public boolean deleteCurso(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete("curso", "curso_id=?", new String[]{String.valueOf(id)});

        return rowsDeleted > 0;
    }
    public boolean insertGaleria(String titulo, String descricao, String imagemBase64) {
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("galeria_titulo", titulo);
            contentValues.put("galeria_descricao", descricao);
            contentValues.put("galeria_imagem", imagemBase64);

            // Inserção da galeria no banco e obtenção do ID da galeria recém-cadastrada
            long idGaleria = db.insertOrThrow("galeria", null, contentValues);
            if (idGaleria != -1) {
                // Log quando a inserção é bem-sucedida
                Log.d("Conexao", "Galeria inserida com sucesso. ID da galeria: " + idGaleria);
            }
            // Retorna true se a inserção foi bem-sucedida
            return idGaleria != -1;
        } catch (SQLiteConstraintException e) {
            Log.e("Conexao", "Erro ao inserir galeria: " + e.getMessage());
            return false;
        } catch (Exception e) {
            Log.e("Conexao", "Erro ao inserir galeria: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (db != null) {

            }
        }
    }
    public List<Galeria> getAllGalerias() {
        List<Galeria> listaGalerias = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query("galeria", null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("galeria_id"));
                String titulo = cursor.getString(cursor.getColumnIndexOrThrow("galeria_titulo"));
                String descricao = cursor.getString(cursor.getColumnIndexOrThrow("galeria_descricao"));
                String imagem = cursor.getString(cursor.getColumnIndexOrThrow("galeria_imagem"));

                Log.d("GaleriaAdapter", "Galeria encontrada: " + titulo); // Adicione este log para verificar se os dados estão corretos

                Galeria galeria = new Galeria(id, titulo, descricao, imagem);
                listaGalerias.add(galeria);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return listaGalerias;
    }
    public boolean updateGaleria(int id, String titulo, String descricao, String imagem) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("galeria_titulo", titulo);
        values.put("galeria_descricao", descricao);

        // Verificar se a imagem foi alterada
        if (imagem != null && !imagem.isEmpty()) {
            values.put("galeria_imagem", imagem);
        }

        int rowsAffected = db.update("galeria", values, "galeria_id=?", new String[]{String.valueOf(id)});

        return rowsAffected > 0;
    }
    public boolean deleteGaleria(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete("galeria", "galeria_id=?", new String[]{String.valueOf(id)});
        db.close();
        return rowsDeleted > 0;
    }


}