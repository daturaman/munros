# Munro Library Challenge

How to use the API
---

To perform a search, call  `MunroFinderService#search`. The method is overloaded, so if you call the no-arg method, all
munros will be returned (at least those with categories). The `Query` class can be constructed using its fluent API and
multiple search criteria can be chained together,
e.g. `query().minHeight(999.9f).maxHeight(1101).sortNameAsc().limitResults(250)`


