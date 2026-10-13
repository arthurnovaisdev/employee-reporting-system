# Ouvidoria Interna - Backend

Backend de uma aplicação corporativa de ouvidoria interna e canal de denúncias, desenvolvida para uso real em uma empresa.

A API foi construída com **Java, Spring Boot, Spring Security, JWT e PostgreSQL**, sendo responsável por autenticação, controle de acesso, gerenciamento de usuários e categorias, registro de denúncias, atualização de status, anexos, alteração e recuperação de senha.

As denúncias podem ser acompanhadas por meio de **protocolo e código de acompanhamento**, mantendo o fluxo separado da identidade do usuário.

O projeto foi desenvolvido considerando boas práticas de segurança, privacidade e princípios da **LGPD**.

## Tecnologias

- Java
- Spring Boot
- Spring Security
- JWT
- Spring Data JPA
- Hibernate
- PostgreSQL
- Maven
- Docker
- Brevo

## Principais funcionalidades

- Autenticação e autorização
- Controle de acesso por perfil
- Cadastro e gerenciamento de usuários
- Gerenciamento de categorias
- Registro de denúncias
- Anexos
- Protocolo e código de acompanhamento
- Consulta de denúncias
- Atualização de status
- Alteração e recuperação de senha
- Envio de e-mails transacionais

## E-mail de contato do administrador inicial

Ao iniciar a API com o perfil `bootstrap-admin`, configure `ADMIN_CPF`,
`ADMIN_INITIAL_PASSWORD` e `ADMIN_CONTACT_EMAIL` no ambiente. O bootstrap cria
o administrador com esse e-mail quando o banco está vazio. Se a conta
administrativa do mesmo CPF já existir e estiver sem e-mail de contato, o
bootstrap preenche somente esse campo, sem alterar a senha. Um e-mail já
cadastrado não é substituído.

O perfil precisa estar ativo na inicialização em que o bootstrap deve rodar
(por exemplo, `SPRING_PROFILES_ACTIVE=prod,bootstrap-admin`). Depois da
inicialização, a recuperação de senha existente pode ser solicitada pelo CPF
na tela de login. O valor de `ADMIN_CONTACT_EMAIL` deve ser um endereço válido
controlado pelo responsável pela conta; não coloque o endereço real no código.

## Autor

Arthur Novais Peixoto
