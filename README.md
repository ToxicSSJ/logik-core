# logik-core [![CI](https://github.com/ToxicSSJ/logik-core/actions/workflows/ci.yml/badge.svg)](https://github.com/ToxicSSJ/logik-core/actions/workflows/ci.yml)

Librería de lógica proposicional y de predicados: analizador de fórmulas y verificador de demostraciones por deducción natural (reglas de Copi, al estilo de LogicCoach).

## Estado

El código actual es el original de 2018, hecho en primer semestre de Ingeniería de Sistemas. Está conservado en la etiqueta [`legacy-2018`](https://github.com/ToxicSSJ/logik-core/tree/legacy-2018) y se va a reescribir.

- De los 96 tests de demostraciones, 77 pasan.
- Los otros 19 están marcados con `@Disabled`, y cada uno explica su motivo: un bug conocido del motor o un error en el propio test. Se reactivarán en la reescritura.

## Compilar y probar

Requiere Java 17 o superior y Maven:

```sh
mvn verify
```

La CI de GitHub Actions corre los tests en cada push y pull request, con Java 17 y 21.

## Licencia

[MIT](LICENSE).
