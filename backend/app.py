from decimal import Decimal
from MySQLdb import Error, MySQLError
from flask import Flask, request, jsonify
from flask_mysqldb import MySQL
from flask_cors import CORS
import bcrypt
from dotenv import load_dotenv
import os
from datetime import datetime, timedelta, timezone
from flask_mail import Message, Mail
import boto3
import random

load_dotenv()
app = Flask(__name__)
CORS(app)

# cinfigurações
app.config['MYSQL_HOST'] = os.getenv('MYSQL_HOST')
app.config['MYSQL_USER'] = os.getenv('MYSQL_USER')     
app.config['MYSQL_PASSWORD'] = os.getenv('MYSQL_PASSWORD')
app.config['MYSQL_DB'] = os.getenv('MYSQL_DB')

app.config['MAIL_SERVER'] = 'smtp.gmail.com'
app.config['MAIL_PORT'] = 587
app.config['MAIL_USE_TLS'] = True
app.config['MAIL_USE_SSL'] = False
app.config['MAIL_USERNAME'] = os.getenv('MAIL_USERNAME')
app.config['MAIL_PASSWORD'] = os.getenv('MAIL_PASSWORD')
app.config['MAIL_DEFAULT_SENDER'] = os.getenv('MAIL_DEFAULT_SENDER')

WASABI_ACCESS_KEY = os.getenv('WASABI_ACCESS_KEY')
WASABI_SECRET_KEY = os.getenv('WASABI_SECRET_KEY')
WASABI_ENDPOINT = os.getenv('WASABI_ENDPOINT')
WASABI_BUCKET_NAME = os.getenv('WASABI_BUCKET_NAME')

s3_client = boto3.client(
    's3',
    aws_access_key_id=WASABI_ACCESS_KEY,
    aws_secret_access_key=WASABI_SECRET_KEY,
    endpoint_url=WASABI_ENDPOINT
)

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

        cursor.execute("""
            SELECT id, userName, password, userPhone, isVerified 
            FROM ad_users 
            WHERE email = %s
        """, (email,))
        result = cursor.fetchone()

        if result:
            user_id, userName, hashed_password, telefone, isVerified = result
            
            if not isVerified:
                return jsonify({"success": False, "message": "Usuário não verificado"}), 403

            if bcrypt.checkpw(senha.encode('utf-8'), hashed_password.encode('utf-8')):
                return jsonify({
                    "success": True,
                    "userId": user_id,
                    "message": "Login realizado com sucesso",
                    "nome": userName,
                    "email": email,
                    "phone": telefone
                }), 200

            return jsonify({"success": False, "message": "E-mail ou senha inválidos"}), 401

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

@app.route('/enviar_email_redefinicao', methods=['POST'])
def enviar_email_redefinicao():
    try:
        data = request.get_json()
        email = data.get('email')

        if not email:
            return jsonify({"success": False, "message": "E-mail é obrigatório"}), 400

        conn = mysql.connection
        cursor = conn.cursor()
        
        code = random.randint(100000, 999999)

        cursor.execute("SELECT id FROM ad_users WHERE email = %s", (email,))
        user = cursor.fetchone()

        if not user:
            return jsonify({"success": False, "message": "Usuário não encontrado"}), 404

        expiration_time = datetime.now(timezone.utc) + timedelta(minutes=2)

        cursor.execute("""
            UPDATE ad_users
            SET reset_token = %s, reset_token_expires = %s
            WHERE email = %s
        """, (code, expiration_time, email))
        conn.commit()
        
        subject = "Redefinição de Senha"
        
        body = f"""
        <html>
            <body>
                <p>Use o seguinte código para redefinir sua senha:</p>
                <h2>{code}</h2>
                <p>Este código é válido por 2 minutos.</p>
            </body>
        </html>
        """
        
        msg = Message(subject, recipients=[email])
        msg.html = body
        
        try:
            mail.send(msg)
            print("E-mail enviado com sucesso!")
            print(f"Código para {email}: {code}")
            return jsonify({"success": True, "message": "E-mail enviado com sucesso"}), 200
        except Exception as e:
            print(f"Erro ao enviar e-mail: {e}")
            return jsonify({"success": False, "message": "Erro ao enviar o e-mail"}), 500
            
    except Exception as e:
        print(f"Erro: {str(e)}")
        return jsonify({"success": False, "message": "Erro ao enviar e-mail"}), 500

@app.route('/validar_codigo_redefinir_senha', methods=['POST'])
def validar_codigo():
    try:
        data = request.get_json()
        email = data.get('email')
        code = data.get('token')
        
        if not email or not code:
            return jsonify({"success": False, "message": "E-mail e código são obrigatórios"}), 400

        conn = mysql.connection
        cursor = conn.cursor()

        cursor.execute("""
            SELECT reset_token, reset_token_expires
            FROM ad_users
            WHERE email = %s
        """, (email,))
        user = cursor.fetchone()

        if not user:
            return jsonify({"success": False, "message": "Usuário não encontrado"}), 404

        reset_code, reset_code_expires = user
        print(f'O reset_code é {reset_code}')

        if reset_code != code:
            return jsonify({"success": False, "message": "Código inválido"}), 400

        if reset_code_expires.tzinfo is None:
            reset_code_expires = reset_code_expires.replace(tzinfo=timezone.utc)

        if datetime.now(timezone.utc) > reset_code_expires:
            return jsonify({"success": False, "message": "Código expirado"}), 400

        cursor.execute("""
            UPDATE ad_users
            SET isVerified = 1
            WHERE email = %s
        """, (email,))
        conn.commit()

        return jsonify({"success": True, "message": "Código validado com sucesso e usuário verificado"}), 200

    except Exception as e:
        print(f"Erro: {str(e)}")
        return jsonify({"success": False, "message": "Erro ao validar código"}), 500

@app.route('/redefinir_senha', methods=['POST'])
def redefinir_senha():
    try:
        data = request.get_json()
        email = data.get('email')
        nova_senha = data.get('novaSenha')
        
        if not email or not nova_senha:
            return jsonify({"success": False, "message": "E-mail e nova senha são obrigatórios"}), 400
        
        hashed_password = bcrypt.hashpw(nova_senha.encode('utf-8'), bcrypt.gensalt())

        conn = mysql.connection
        cursor = conn.cursor()

        cursor.execute("""
            UPDATE ad_users
            SET password = %s
            WHERE email = %s
        """, (hashed_password, email))
        conn.commit()

        return jsonify({"success": True, "message": "Senha atualizada com sucesso!"}), 200

    except Exception as e:
        print(f"Erro: {str(e)}")
        return jsonify({"success": False, "message": "Erro ao validar código"}), 500

@app.route('/enviar_email_validação', methods=['POST'])
def enviar_email_validacao():
    try:
        data = request.get_json()
        email = data.get('email')

        if not email:
            return jsonify({"success": False, "message": "E-mail é obrigatório"}), 400

        conn = mysql.connection
        cursor = conn.cursor()
        
        code = random.randint(100000, 999999)

        cursor.execute("SELECT id FROM ad_users WHERE email = %s", (email,))
        user = cursor.fetchone()

        if not user:
            return jsonify({"success": False, "message": "Usuário não encontrado"}), 404

        expiration_time = datetime.now(timezone.utc) + timedelta(minutes=2)

        cursor.execute("""
            UPDATE ad_users
            SET verificationToken = %s, reset_token_expires = %s
            WHERE email = %s
        """, (code, expiration_time, email))
        conn.commit()
        
        subject = "Código de validação de conta"
        
        body = f"""
        <html>
            <body>
                <p>Use o seguinte código para validar sua conta:</p>
                <h2>{code}</h2>
                <p>Este código é válido por 2 minutos.</p>
            </body>
        </html>
        """
        
        msg = Message(subject, recipients=[email])
        msg.html = body
        
        try:
            mail.send(msg)
            print("E-mail enviado com sucesso!")
            print(f"Código para {email}: {code}")
            return jsonify({"success": True, "message": "E-mail enviado com sucesso"}), 200
        except Exception as e:
            print(f"Erro ao enviar e-mail: {e}")
            return jsonify({"success": False, "message": "Erro ao enviar o e-mail"}), 500
            
    except Exception as e:
        print(f"Erro: {str(e)}")
        return jsonify({"success": False, "message": "Erro ao enviar e-mail"}), 500

@app.route('/validar_codigo_validacao_email', methods=['POST'])
def validar_codigo_email():
    try:
        data = request.get_json()
        email = data.get('email')
        code = data.get('token')
        
        if not email or not code:
            return jsonify({"success": False, "message": "E-mail e código são obrigatórios"}), 400

        conn = mysql.connection
        cursor = conn.cursor()

        cursor.execute("""
            SELECT verificationToken, reset_token_expires
            FROM ad_users
            WHERE email = %s
        """, (email,))
        user = cursor.fetchone()

        if not user:
            return jsonify({"success": False, "message": "Usuário não encontrado"}), 404

        verification_code, reset_code_expires = user
        print(f'O verification_code é {verification_code}')

        if verification_code != code:
            return jsonify({"success": False, "message": "Código inválido"}), 400

        if reset_code_expires.tzinfo is None:
            reset_code_expires = reset_code_expires.replace(tzinfo=timezone.utc)

        if datetime.now(timezone.utc) > reset_code_expires:
            return jsonify({"success": False, "message": "Código expirado"}), 400
        
        cursor.execute("""
            UPDATE ad_users
            SET isVerify = 1
            WHERE email = %s
        """, (email,))
        conn.commit()
        
        return jsonify({"success": True, "message": "Código validado com sucesso"}), 200

    except Exception as e:
        print(f"Erro: {str(e)}")
        return jsonify({"success": False, "message": "Erro ao validar código"}), 500

@app.route('/images', methods=['GET'])
def get_images():
    try:
        conn = mysql.connection
        cursor = conn.cursor()
        
        query = "SELECT id, url, alt_text FROM images WHERE status = 'active' LIMIT 7"
        
        cursor.execute(query)
        images = cursor.fetchall()
        
        results = []
        for row in images:
            result = {
                'id': row[0],
                'url': row[1],
                'alt_text': row[2]
            }
            results.append(result)
        
        return jsonify(results), 200
    except Exception as e:
        print(f"Erro ao buscar imagens: {str(e)}")
        return jsonify({"error": str(e)}), 500
    finally:
        cursor.close()
        
@app.route("/produto/<int:produto_id>")
def get_produto(produto_id):
    try:
        conn = mysql.connection
        cursor = conn.cursor()

        query = """
        SELECT caption, price, format, upload_date, url, uploaded_by, size 
        FROM images WHERE id = %s
        """
        cursor.execute(query, (produto_id,))
        produto = cursor.fetchone()
        
        dados_produto = {
            'id': produto_id,
                'nome': produto[0],
                'preco': str(Decimal(produto[1])),
                'formatos': produto[2],
                'dataPublicacao': produto[3].strftime("%d-%m-%Y") if produto[3] else None,
                'url': produto[4],
                'dono': produto[5],
                'tamanho': str(produto[6])
        }
        
        print(dados_produto)
        if produto:
            return jsonify(dados_produto), 200
        else:
            return jsonify({'message': 'Produto não encontrado'}), 404

    except MySQLError as e:
        print(f"Erro de MySQL: {str(e)}")
        return jsonify({"success": False, "message": "Erro no banco de dados"}), 500
    except Exception as e:
        print(f"Erro geral: {str(e)}")
        return jsonify({"success": False, "message": "Erro interno no servidor"}), 500

@app.route('/search/images', methods=['GET'])
def search_images():
    # Pega os parâmetros da query
    tag = request.args.get('tag', '')
    isPremium = request.args.get('isPremium', 'false').lower() == 'true'
    isGratis = request.args.get('isGratis', 'false').lower() == 'true'
    formats = request.args.getlist('formats')  

    # Inicia a conexão com o banco de dados
    conn = mysql.connection
    cursor = conn.cursor()

    # Cria a base da query SQL
    query = """
        SELECT id, url, alt_text 
        FROM images 
        WHERE (caption LIKE %s OR texture LIKE %s OR alt_text LIKE %s)
    """
    params = ['%' + tag + '%'] * 3

    # Adiciona condições de premium e grátis à query, se necessário
    if isPremium and not isGratis:
        query += " AND license = 'premium'"
    elif isGratis and not isPremium:
        query += " AND license = 'free'"

    # Adiciona a condição de formatos à query, se necessário
    if formats:
        placeholders_formats = ', '.join(['%s'] * len(formats))
        query += f" AND (format IN ({placeholders_formats}) OR type IN ({placeholders_formats}))"
        params.extend(formats)
        params.extend(formats)

    # Executa a query no banco
    cursor.execute(query, params)

    # Recupera os resultados
    results = cursor.fetchall()

    # Cria uma lista para armazenar as imagens encontradas
    images = []
    for row in results:
        images.append({
            'id': row[0],
            'url': row[1],
            'alt_text': row[2]
        })

    # Fecha a conexão
    cursor.close()

    # Retorna a resposta como JSON
    return jsonify(images)


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)
    