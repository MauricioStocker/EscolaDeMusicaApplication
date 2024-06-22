Aplicativo Escola de Música
Visão Geral
Este projeto utiliza o Android Studio com Java e SQLite como banco de dados para desenvolver um aplicativo destinado à gestão de uma escola de música. O aplicativo permite o cadastro de professores, alunos e administradores, além de gerenciar notas, faltas e outras informações essenciais para a gestão educacional.

Requisitos do Sistema
Android Studio: id("com.android.application") version "8.2.2"
Java: sourceCompatibility = JavaVersion.VERSION_1_8, targetCompatibility = JavaVersion.VERSION_1_8
Banco de Dados: SQLite
Funcionalidades Principais
Cadastro de Professores:

Inicialização do sistema com o cadastro de professores utilizando informações como nome, matéria e senha virtual.
Cadastro de Alunos:

Após o cadastro de professores, permite o cadastro de alunos, atendendo às regras de negócio estabelecidas. Cada aluno recebe sua matéria virtual para acesso ao conteúdo educacional.
Acesso do Administrador:

Utilização das informações do coordenador para criar acesso administrativo. Durante os testes, utilize o CPF: XXXXXXXX e a senha: admin. A entrada da senha é tratada de forma que maiúsculas e minúsculas sejam aceitas.
Funcionalidades Específicas:

Gerenciamento completo de alunos, incluindo controle de faltas e notas.
Os alunos podem visualizar suas notas e faltas, além de poderem adicionar ou modificar sua foto de perfil.
Gratuidade e Adaptabilidade:

O aplicativo é totalmente gratuito e pode ser adaptado para qualquer escola ou instituição educacional.
APIs Utilizadas:

Integração de APIs para consulta de CEP, envio de mensagens pelo WhatsApp e cadastro de datas de nascimento utilizando um calendário.
Pronto para Entrega
O aplicativo está finalizado e pronto para ser entregue ao final do semestre. Com uma arquitetura robusta em Java, integração com SQLite e APIs externas, o projeto atende plenamente aos requisitos estabelecidos para uma aplicação educacional moderna e eficiente.

