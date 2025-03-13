# Plataforma de Comercialização de Ativos Digitais para Design Gráfico

Este projeto tem como objetivo o desenvolvimento de uma **plataforma completa para a comercialização de ativos digitais voltados para design gráfico**, oferecendo modelos de **venda por assinatura e compra individual**, semelhante a plataformas como **Freepik** e **Designi**. O projeto cobre desde a concepção da interface do usuário até a implementação da infraestrutura backend, garantindo uma experiência fluida e segura para os usuários.

## Tecnologias Utilizadas

### Backend - API com Flask
A API do projeto foi desenvolvida utilizando **Flask**, um microframework para Python que permite a criação de aplicações web de forma rápida e escalável. O Flask é responsável por:

- Gerenciamento de usuários (cadastro, login, autenticação JWT)
- Processamento de requisições para compra e assinatura
- Integração com o banco de dados
- Upload e manipulação de imagens

### Banco de Dados - MySQL (Workbench)
O armazenamento de dados é feito utilizando **MySQL**, gerenciado por meio do **MySQL Workbench**, permitindo a criação e manutenção das tabelas, procedimentos armazenados e consultas otimizadas.
O banco de dados é estruturado para armazenar:

- Usuários e suas informações
- Ativos digitais (imagens, vetores, templates)
- Planos de assinatura e histórico de compras
- Transações e logs de atividade

### Armazenamento de Imagens - Wasabi
O **Wasabi** é utilizado como serviço de armazenamento de imagens na nuvem, garantindo um armazenamento escalável, acessível e seguro. As imagens dos ativos digitais são enviadas para o Wasabi e armazenadas em **buckets**, garantindo que os usuários possam acessá-las de forma rápida e eficiente. O processo envolve:

## Funcionalidades

- 🛒 **Modelo de monetização flexível**: Compra individual ou planos de assinatura.
- 🔐 **Segurança e autenticação**: Proteção de dados com JWT e controle de acesso.

---

