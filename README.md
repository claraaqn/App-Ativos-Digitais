# Plataforma de Comercialização de Ativos Digitais para Design Gráfico

Este projeto tem como objetivo o desenvolvimento de uma plataforma completa para a comercialização de ativos digitais voltados para design gráfico, oferecendo modelos de venda por assinatura e compra individual, semelhante a plataformas como Freepik e Designi.
Além disso, o projeto surge como solução para um problema comum nesse mercado: a falta de versões mobile em muitas plataformas de ativos digitais, o que limita a acessibilidade e a experiência dos usuários. Este aplicativo busca preencher essa lacuna, proporcionando uma experiência otimizada para dispositivos móveis, permitindo que designers e criadores naveguem, visualizem e adquiram ativos de forma rápida e intuitiva diretamente de seus smartphones.

## 🏗️ Arquitetura do Projeto

### Backend - API com Flask
A API do projeto foi desenvolvida utilizando **Flask**, um microframework para Python que permite a criação de aplicações web de forma rápida e escalável. O Flask é responsável por:

- Gerenciamento de usuários (cadastro, login, autenticação JWT)
- Processamento de requisições para compra e assinatura
- Integração com o banco de dados
- Upload e manipulação de imagens

### Banco de Dados - MySQL
O armazenamento de dados é feito utilizando **MySQL**, gerenciado por meio do **MySQL Workbench**, permitindo a criação e manutenção das tabelas, procedimentos armazenados e consultas otimizadas.
O banco de dados é estruturado para armazenar:

- Usuários e suas informações
- Ativos digitais (imagens, vetores, templates)
- Planos de assinatura e histórico de compras
- Transações e logs de atividade

### Armazenamento de Imagens - Wasabi
O **Wasabi** é utilizado como serviço de armazenamento de imagens na nuvem, garantindo um armazenamento escalável, acessível e seguro. As imagens dos ativos digitais são enviadas para o Wasabi e armazenadas em **buckets**, garantindo que os usuários possam acessá-las de forma rápida e eficiente.

### Frontend - Android (Kotlin)
O aplicativo Android foi desenvolvido em **Kotlin** utilizando:
- **Jetpack Compose** para UI moderna
- **Retrofit** para comunicação com a API
- **Room** para banco de dados local
- **Glide** para carregamento de imagens
- **AWS SDK** para integração com Wasabi

## 📋 Funcionalidades

- 🛒 **Modelo de monetização flexível**: Compra individual ou planos de assinatura
- 🔐 **Segurança e autenticação**: Proteção de dados com JWT e controle de acesso
- 📱 **Aplicativo Android nativo**: Interface moderna e responsiva
- ☁️ **Armazenamento em nuvem**: Integração com Wasabi para imagens
- 📧 **Sistema de email**: Verificação de conta e recuperação de senha
- 🔍 **Sistema de busca**: Filtros por categoria, cor, formato e licença
- ❤️ **Sistema de favoritos**: Usuários podem salvar itens favoritos
- 👥 **Perfis de colaboradores**: Visualização de trabalhos de designers

## 🗄️ Estrutura do Banco de Dados

### Tabelas Principais
- `ad_users`: Usuários da plataforma
- `images`: Ativos digitais (imagens, vetores, templates)
- `image_tags`: Tags para categorização
- `favorites`: Favoritos dos usuários
- `downloads`: Histórico de downloads
- `follows`: Relacionamentos entre usuários

## 🔧 Configuração Manual (Sem Docker)

### Backend
1. Instale Python 3.11+
2. Instale MySQL 8.0+
3. Configure as variáveis de ambiente
4. Instale as dependências: `pip install -r backend/requirements.txt`
5. Execute: `python backend/app.py`

### Android
1. Instale Android Studio
2. Abra o projeto em `app/`
3. Configure o IP do backend em `RetrofitInstance.kt`
4. Build e execute o projeto

## 📝 Endpoints da API

### Autenticação
- `POST /register` - Cadastro de usuário
- `POST /login` - Login de usuário
- `POST /change_password` - Alterar senha

### Usuários
- `GET /user/{id}` - Obter perfil do usuário
- `POST /update_profile` - Atualizar perfil
- `DELETE /delete_user/{id}` - Deletar usuário

### Imagens
- `GET /images` - Listar todas as imagens
- `GET /produto/{id}` - Obter detalhes de uma imagem
- `GET /search/images` - Buscar imagens com filtros
- `GET /imagens/categoria/{categoria}` - Imagens por categoria

### Favoritos
- `POST /add_favorite` - Adicionar favorito
- `POST /remove_favorite` - Remover favorito
- `GET /check_if_liked` - Verificar se curtido

### Email
- `POST /enviar_email_redefinicao` - Enviar email de recuperação
- `POST /validar_codigo_redefinir_senha` - Validar código
- `POST /redefinir_senha` - Redefinir senha
- `POST /enviar_email_validação` - Enviar email de validação
- `POST /validar_codigo_validacao_email` - Validar email

## 🤝 Contribuição

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## 📄 Licença

Este projeto está sob a licença CC BY-NC-ND 4.0. Veja o arquivo [LICENSE-CC-BY-NC-ND-4.0.md](LICENSE-CC-BY-NC-ND-4.0.md) para mais detalhes.

## 📞 Suporte

Para suporte, envie um email para clara.aquino@ufrpe.br ou abra uma issue no repositório.

---
