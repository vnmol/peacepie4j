# peacepie4j

## Единый запуск OSGi-плагинов

`peacepie-plugin-test` — хост Apache Felix и единая точка запуска плагинов.
Каждый плагин является OSGi-бандлом и может объявлять **один или несколько**
классов, реализующих `ru.peacepie.plugin.api.PeacepieHandler`.

```java
public class MyHandler implements PeacepieHandler {
    public MyHandler() { }

    @Override
    public void handle(String msg) {
        System.out.println(msg);
    }
}
```

### Объявление обработчиков

Перечислите полные имена классов через запятую в заголовке `Peacepie-Handlers`
манифеста OSGi-бандла:

```text
Peacepie-Handlers: my.plugin.FirstHandler,my.plugin.SecondHandler,my.plugin.ThirdHandler
```

В `peacepie-plugin-echo` приведён пример одного плагина с тремя обработчиками:

- `EchoHandler` — выводит исходное сообщение;
- `StatisticsHandler` — выводит длину сообщения;
- `LoggingHandler` — выводит сообщение с временной меткой.

Для каждого класса из заголовка хост создаёт **отдельный экземпляр** через
публичный конструктор без аргументов и добавляет его в общий `HandlerRegistry`.
После запуска бандлов хост вызывает `handle(message)` у всех зарегистрированных
обработчиков. Ошибка одного обработчика логируется и не прерывает вызов остальных.

Для совместимости хост также распознаёт устаревший заголовок `Peacepie-Handler`
с одним именем класса. Для новых плагинов используйте только `Peacepie-Handlers`.

Плагинам не требуется собственный `Bundle-Activator`: установкой, запуском,
регистрацией обработчиков и остановкой OSGi-бандлов управляет хост.

### Добавление нового плагина

1. Добавьте зависимость `peacepie-plugin-api` со scope `provided`.
2. Реализуйте один или несколько классов `PeacepieHandler` с публичным
   конструктором без аргументов.
3. Укажите классы в `Peacepie-Handlers` в конфигурации `maven-bundle-plugin`.
4. Соберите бандл и поместите JAR в каталог `peacepie-plugin-test/plugins`.

### Сборка

Из корня репозитория:

```bash
mvn clean install
mkdir -p peacepie-plugin-test/plugins
cp peacepie-plugin-echo/target/peacepie-plugin-echo-0.0.1.jar peacepie-plugin-test/plugins/
```

### Запуск

```bash
cd peacepie-plugin-test
mvn exec:java
```

По умолчанию хост ищет JAR-файлы в `plugins`. Первый аргумент командной строки
переопределяет каталог плагинов, второй — сообщение, передаваемое каждому
обработчику. Например:

```bash
mvn exec:java -Dexec.args="plugins Hello-Peacepie"
```

### Реестр обработчиков

`HandlerRegistry` находится в `peacepie-plugin-test` и хранит связь между
OSGi-бандлом, именем класса и экземпляром `PeacepieHandler`. Реестр предоставляет
количество и неизменяемое представление зарегистрированных обработчиков,
выполняет общий вызов и очищается перед остановкой бандлов.
