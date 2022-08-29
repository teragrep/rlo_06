# teragrep RFC5424 parser
Features
* Fast

Non-features
* Excellent strictness


## License
AGPLv3 with link:https://github.com/teragrep/rlo_06/blob/master/LICENSE#L665-L670[additional permissions] granted in the license.


## Usage
This parser uses subscriptions to pick up interesting message elements
from the message stream.

For generic message property extraction use:
```java
RFC5424ParserSubscription subscription = new RFC5424ParserSubscription();
subscription.subscribeAll();
// or following where parameter is type of ParserEnum
subscription.add(ParserEnum.TIMESTAMP);
subscription.add(ParserEnum.MSG);
```

For structured data property extraction use:
```java
RFC5424ParserSDSubscription sdSubscription = new RFC5424ParserSDSubscription();
// subscribe with following where first is SD-ID and second is PARAM-NAME
sdSubscription.subscribeElement("event_key@12345","paramKey");
```

After desired subscriptions are created then allocate a resultset object:
```java
ParserResultset res = new ParserResultset(subscription, sdSubscription);
```

Create a parser with the desired InputStream
```java
RFC5424Parser parser = new RFC5424Parser(inputStream);
```

```java
if (parser.next(res) == true) {
  String msg = res.getMsgAsUTF8String();
  String sdValue = res.getSdValueAsUTF8String("event_key@12345","paramKey")
}
```

Next event can be extracted as follows
```java
res.clear(); // clear resultset before calling .next() again
if(parser.next(res) == true) {
  String msg = res.getMsgAsUTF8String();
  String sdValue = res.getSdValueAsUTF8String("event_key@12345","paramKey")
}
```

## Todo
```
RFC5424Parser.java
```
* Take maximum allocation sizes from ParserResultset for string length checking
* Throw if maximum size is exceeded
* Throw if quotation exceed maximum line. THIS STALLS AT THE MOMENT.
* Honour the subscription map
* Extract rest of the switch case states from the next() to private methods
* Try extracting sdParsing to a dedicated class
* Try extracting buffer handling to a dedicated class

```
SytanxTest.java
```
* More test cases, see palindromicity code

