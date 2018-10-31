package dbv.trick.controledecaixa.Model;


public class Usuario {
    private String nome;
    private String senha;
    private String telefone;
    private String email;
    private String msg_erro;

    public Usuario() {
    }

    public String getMsg_erro() {
        return msg_erro;
    }

    public void setMsg_erro(String msg_erro) {
        this.msg_erro = msg_erro;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
