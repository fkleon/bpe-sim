bpe-sim
=======

An integrated business process monitoring and recommendation engine fed by a simulation model.

### Structure

The project is internally structured in its three main components:
- Business Process Execution and Monitoring
- Business Process Simulation
- Recommendation

### Developers
This project uses Maven. Dependencies can be found in the [pom.xml](pom.xml).

Currently there are some more dependencies which have not been published as maven artifacts and need to be downloaded manually:
- [OpenXES][openxes] (v2.0)
- [DESMO-J][desmoj] (v2.4.1)
- [JSAT][java-statistical-analysis-tool] (latest from svn: r853)
- [Java-ML][java-ml] (latest 0.1.8 source from git: fe768315ff)

[openxes]: http://www.xes-standard.org/openxes/download
[desmoj]: http://desmoj.sourceforge.net/home.html
[java-statistical-analysis-tool]: https://code.google.com/p/java-statistical-analysis-tool/
[java-ml]: http://java-ml.sourceforge.net/
[weka]: http://www.cs.waikato.ac.nz/~ml/weka/
