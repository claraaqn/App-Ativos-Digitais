from flask import Flask, request, jsonify
from flask_mysqldb import MySQL
from flask_cors import CORS
import bcrypt

app = Flask(__name__)
CORS(app)

app.config['MYSQL_HOST'] = '26.158.46.162' 
app.config['MYSQL_USER'] = 'dev_user'      
app.config['MYSQL_PASSWORD'] = '5QKGMhFnnEikbv4'       
app.config['MYSQL_DB'] = 'ativos_digitais'  

mysql = MySQL(app)

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
        verificado = data.get('isVerify', False)
        
        hashed_password = bcrypt.hashpw(senha.encode('utf-8'), bcrypt.gensalt())

        conn = mysql.connection
        cursor = conn.cursor()
        query  = """
        INSERT INTO ad_users (userName, email, password, userProfile, userCape, id_endereco, userDescription, userPhone, userDate, isVerify)
        VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
        """
        cursor.execute(query, (nome, email, hashed_password, profile, cape, id_endereco, descripition, telefone, date, verificado))
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

        cursor.execute("SELECT password FROM ad_users WHERE email = %s", (email,))
        result = cursor.fetchone()

        if result and bcrypt.checkpw(senha.encode('utf-8'), result[0].encode('utf-8')):
            return jsonify({"success": True, "message": "Login realizado com sucesso"}), 200
        else:
            return jsonify({"success": False, "message": "E-mail ou senha inválidos"}), 401

    except Exception as e:
        print(f"Erro: {str(e)}")
        return jsonify({"success": False, "message": "Erro interno no servidor"}), 500


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)