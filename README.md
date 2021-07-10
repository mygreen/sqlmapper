# SqlMapper

**SqlMapper** は、Spring Framework の JDBC関連の機能を使って 「S2JDBC の再実装 + 機能追加」 を目指した O/R Mapperのライブラリです。

- アノテーションにより簡単にエンティティ用クラスを定義することができます。
  - JPAのアノテーションを参考に本ライブラリ独自に作成しているため、JPAを知っていれば理解しやすい。
- エンティティのメタモデルを自動生成し、それを使うことによってある程度のタイプセーフにクエリを組み立てることができます。
  - メタモデルは、 [QueryDSL](http://www.querydsl.com/) を参考にしています。
- 2Way-SQLと呼ばれる SQLテンプレートを使用し、複雑なクエリを記述することができます。
  - 2Way-SQLのライブラリとして、[splate](https://mygreen.github.io/splate/) を利用しています。
- Spring Framework ネイティブのため、他のDBアクセスフレームワークと異なり、トランザクションやデータソースなどをブリッジするための無駄な設定なく利用できます。
  - Spring Boot 用の機能も提供しており、より簡単に利用できるようになっています。
- リレーションのマッピングには対応していません。
- 現状の使い勝手は、 [Doma2](https://doma.readthedocs.io/en/latest/) の Criteria API に近いものとなっています。

## ライセンス

Apache License verion 2.0

## 前提条件

- Java 11+
- Srping Framework 5.3+
- Spring Boot 2.4+
- splate 0.2+

## ドキュメント

- 使用方法
  - https://mygreen.github.io/sqlmapper/how2docs/index.html
- プロジェクト情報
  - https://mygreen.github.io/sqlmapper/
- JavaDoc
  - https://mygreen.github.io/sqlmapper/apidocs/index.html



