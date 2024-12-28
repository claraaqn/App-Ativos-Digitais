import uuid
from flask import Flask, request, jsonify
from flask_mysqldb import MySQL
from flask_cors import CORS
import bcrypt
import dotenv
from datetime import datetime, timedelta, timezone
import secrets
from flask_mail import Message, Mail


app = Flask(__name__)
CORS(app)

# criar em arquivo .env para puxar as informações de configurações
# criar .gitignore do .env
app.config['MYSQL_HOST'] = '26.246.225.30' 
app.config['MYSQL_USER'] = 'dev_user'      
app.config['MYSQL_PASSWORD'] = '5QKGMhFnnEikbv4'       
app.config['MYSQL_DB'] = 'ativos-digitais'  

app.config['MAIL_SERVER'] = 'smtp.gmail.com'
app.config['MAIL_PORT'] = 587
app.config['MAIL_USE_TLS'] = True
app.config['MAIL_USE_SSL'] = False
app.config['MAIL_USERNAME'] = 'vigorize0@gmail.com'
app.config['MAIL_PASSWORD'] = 'rcexxotmxjlxlrna'
app.config['MAIL_DEFAULT_SENDER'] = 'vigorize0@gmail.com'


mysql = MySQL(app)
mail = Mail(app)

@app.route('/register', methods=['POST'])
def cadastrar_usuario():
    try:
        data = request.get_json()
        
        nome = data.get('userName')
        email = data.get('email')
        senha = data.get('password')
        profile = data.get('userProfile', None)
        cape = data.get('userCape', None)
        id_endereco = data.get('id_endereco', None)
        descripition = data.get('userDescription', None)
        telefone = data.get('userPhone')
        date = data.get('userDate')
        stripe_customer_id = data.get('stripe_customer_id', None)
        reset_token = data.get('reset_token', None)
        reset_token_expires = data.get('reset_token_expires', None)
        provider = data.get('provider', None)
        created_at = data.get('created_at', None)
        verificationToken = data.get('verificationToken', False)
        verificado = data.get('isVerified', False)
        
        hashed_password = bcrypt.hashpw(senha.encode('utf-8'), bcrypt.gensalt())

        conn = mysql.connection
        cursor = conn.cursor()
        query  = """
        INSERT INTO ad_users (userName, email, password, userProfile, userCape, id_endereco, userDescription, userPhone, userDate, isVerified,
        stripe_customer_id, reset_token, reset_token_expires, provider, created_at, verificationToken)
        VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
        """
        cursor.execute(query, (nome, email, hashed_password, profile, cape, id_endereco, descripition, telefone, date, verificado,
                               stripe_customer_id, reset_token, reset_token_expires, provider, created_at, verificationToken))
        conn.commit()

        return jsonify({"success": True, "message": "Usuário cadastrado com sucesso"}), 201

    except Exception as e:
        print(f"Erro: {str(e)}")  # Adicione esse print para ver o erro no console
        return jsonify({"success": False, "message": str(e)}), 500
    
@app.route('/login', methods=['POST'])
def login_usuario():
    try:
        data = request.get_json()
        email = data.get('email')
        senha = data.get('password')

        conn = mysql.connection
        cursor = conn.cursor()

        cursor.execute("SELECT id, userName, password, userPhone FROM ad_users WHERE email = %s", (email,))
        result = cursor.fetchone()

        if result:
            user_id, userName, hashed_password, telefone = result
            if bcrypt.checkpw(senha.encode('utf-8'), hashed_password.encode('utf-8')):
                return jsonify({
                    "success": True,
                    "userId": user_id,
                    "message": "Login realizado com sucesso",
                    "nome": userName,
                    "email": email,
                    "phone": telefone
                }), 200 
            return jsonify({"success": True, "message": "Login realizado com sucesso"}), 200
        else:
            return jsonify({"success": False, "message": "E-mail ou senha inválidos"}), 401

    except Exception as e:
        print(f"Erro: {str(e)}")
        return jsonify({"success": False, "message": "Erro interno no servidor"}), 500

@app.route('/user/<int:id>', methods=['GET'])
def get_user(id):
    try:
        conn = mysql.connection
        cursor = conn.cursor()

        cursor.execute("SELECT userName, email FROM ad_users WHERE id = %s", (id,))
        user = cursor.fetchone()

        if user:
            return jsonify({'id': id, 'name': user[0], 'email': user[1]}), 200
        else:
            return jsonify({'message': 'User not found'}), 404

    except Exception as e:
        print(f"Erro: {str(e)}")
        return jsonify({"success": False, "message": "Erro interno no servidor"}), 500

@app.route('/update_profile', methods=['POST'])
def update_profile():
    try:
        data = request.get_json()
        user_id = data.get('userId')
        nome = data.get('nome')
        email = data.get('email')
        telefone = data.get('phone')

        conn = mysql.connection
        cursor = conn.cursor()
        cursor.execute("""
            UPDATE ad_users
            SET userName = %s, email = %s, userPhone = %s
            WHERE id = %s
        """, (nome, email, telefone, user_id))
        conn.commit()

        return jsonify({"success": True, "message": "Perfil atualizado com sucesso"})
    except Exception as e:
        print(f"Erro: {str(e)}")
        return jsonify({"success": False, "message": "Erro interno no servidor"})

@app.route('/delete_user/<int:id>', methods=['DELETE'])
def delete_user(id):
    try:
        conn = mysql.connection
        cursor = conn.cursor()
        cursor.execute("DELETE FROM ad_users WHERE id = %s", (id,))
        conn.commit()

        return jsonify({"success": True, "message": "Usuário excluído com sucesso"}), 200

    except Exception as e:
        print(f"Erro: {str(e)}")
        return jsonify({"success": False, "message": "Erro interno no servidor"}), 500

@app.route('/change_password', methods=['POST'])
def change_password():
    try:
        data = request.get_json()

        user_id = data.get("userId")
        current_password = data.get("senha_atual")
        new_password = data.get("nova_senha")

        if not user_id or not current_password or not new_password:
            return jsonify({"success": False, "message": "Todos os campos são obrigatórios"}), 400

        conn = mysql.connection
        cursor = conn.cursor()

        cursor.execute("SELECT password FROM ad_users WHERE id = %s", (user_id,))
        result = cursor.fetchone()

        if not result:
            return jsonify({"success": False, "message": "Usuário não encontrado"}), 404

        stored_password = result[0]

        if not bcrypt.checkpw(current_password.encode('utf-8'), stored_password.encode('utf-8')):
            return jsonify({"success": False, "message": "Senha atual incorreta"}), 401
        

        hashed_new_password = bcrypt.hashpw(new_password.encode('utf-8'), bcrypt.gensalt())

        # Atualiza a senha no banco de dados
        cursor.execute("UPDATE ad_users SET password = %s WHERE id = %s", (hashed_new_password, user_id))
        conn.commit()

        return jsonify({"success": True, "message": "Senha alterada com sucesso"}), 200

    except Exception as e:
        print(f"Erro: {str(e)}")
        return jsonify({"success": False, "message": "Erro interno no servidor"}), 500

#ESQUECI SENHA
@app.route('/enviar_email_redefinicao', methods=['POST'])
def enviar_email_redefinicao():
    try:
        data = request.get_json()
        email = data.get('email')

        if not email:
            return jsonify({"success": False, "message": "E-mail é obrigatório"}), 400

        conn = mysql.connection
        cursor = conn.cursor()

        cursor.execute("SELECT id FROM ad_users WHERE email = %s", (email,))
        user = cursor.fetchone()

        if not user:
            return jsonify({"success": False, "message": "Usuário não encontrado"}), 404

        # Gera o token de redefinição
        token = secrets.token_urlsafe(32)
        expiration_time = datetime.now(timezone.utc) + timedelta(hours=1)

        # Atualiza o token no banco de dados
        cursor.execute("""
            UPDATE ad_users
            SET reset_token = %s, reset_token_expires = %s
            WHERE email = %s
        """, (token, expiration_time, email))
        conn.commit()

        # Envia o e-mail
        link = f"myapp://resetpassword?token={token}"
        subject = "Redefinição de Senha"
        body = f"Use este link para redefinir sua senha: {link}"

        msg = Message(subject, recipients=[email], body=body)
        try:
            mail.send(msg)
        except Exception as e:
            print(f"Erro ao enviar e-mail: {str(e)}")
            return jsonify({"success": False, "message": "Erro ao enviar e-mail"}), 500

        return jsonify({"success": True, "message": "E-mail enviado com sucesso"}), 200

    except Exception as e:
        print(f"Erro: {str(e)}")
        return jsonify({"success": False, "message": "Erro ao enviar e-mail"}), 500


@app.route('/redefinir_senha', methods=['POST'])
def redefinir_senha():
    try:
        data = request.get_json()
        token = data.get('token')
        nova_senha = data.get('novaSenha')

        if not token or not nova_senha:
            return jsonify({"success": False, "message": "Token e nova senha são obrigatórios"}), 400

        conn = mysql.connection
        cursor = conn.cursor()

        cursor.execute("""
            SELECT id, reset_token_expires
            FROM ad_users
            WHERE reset_token = %s
        """, (token,))
        user = cursor.fetchone()

        if not user:
            return jsonify({"success": False, "message": "Token inválido"}), 400

        user_id, token_expires = user

        if datetime.now(timezone.utc) > token_expires:
            return jsonify({"success": False, "message": "Token expirado"}), 400

        # Atualiza a senha no banco
        hashed_password = bcrypt.hashpw(nova_senha.encode('utf-8'), bcrypt.gensalt())
        cursor.execute("""
            UPDATE ad_users
            SET password = %s, reset_token = NULL, reset_token_expires = NULL
            WHERE id = %s
        """, (hashed_password, user_id))
        conn.commit()

        return jsonify({"success": True, "message": "Senha redefinida com sucesso"}), 200

    except Exception as e:
        print(f"Erro: {str(e)}")
        return jsonify({"success": False, "message": "Erro ao redefinir senha"}), 500


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)
    