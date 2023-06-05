# Getting Started

1. Запускаем команду ./gradlew dockerBuildImage
2. Запускаем docker-compose up
3. Для запуска загрузки вызываем GET ручку http://localhost:8080/file-loader/files/start
4. Для остановки вызываем GET ручку http://localhost:8080/file-loader/files/stop, в этот момент загрузятся оставшиеся файлы загрузятся и программа остановит загрузку. 
