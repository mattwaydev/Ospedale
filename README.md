# Ospedale — Refactor MVC (Parcial 3 POO)

Universidad del Norte — NRC: 2040

## Integrantes
- Samuel Ramirez Nuñez — Layer: Model
- Mateo Carrascal Rodriguez — Layer: Controller
- Sebastian Andres Anaya Ojeda — Layer: View / Navigation / Entry point

## Cómo correr
Requiere Java 21+, Ant, NetBeans (o JDK + Ant standalone).

```bash
ant clean
ant compile
ant run
```

El main class es `com.uninorte.ospedale.Main`. Datos seed en `json/users.json`.

## Arquitectura
- `model/`: entidades, DTOs, repositorios (interfaces + InMemory), loaders JSON.
- `controller/`: lógica de negocio, validators, Response<T> pattern para retorno.
- `view/`: vistas Swing, navegación centralizada en `ViewNavigator`.

## Credenciales seed (json/users.json)
- Admin: `admin_root` / `Admin@1234`
- Patient: `jgarcia90` / `Pass@1234`
- Doctor: `dr_aguirre` / `Doc@1234`
