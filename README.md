# CampusRetrouve

A Lost & Found Management Platform for Campus Communities.

## Database configuration

CampusRetrouve reads its database configuration from environment variables at runtime.

Required:

- `DB_URL` — JDBC connection URL
- `DB_USER` — database user

Optional:

- `DB_PASSWORD` — database password. If it is not defined, an empty password is used for local development.

Example local configuration:

```text
DB_URL=jdbc:mysql://localhost:3306/campus_retrouve?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8
DB_USER=root
DB_PASSWORD=
```

Real database credentials must never be committed to the repository.

`.env` files are ignored by Git, but CampusRetrouve reads operating system environment variables directly; it does not automatically load `.env` files.
