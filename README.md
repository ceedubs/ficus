# Project has moved

The official repository is now [iheartradio/ficus](https://github.com/iheartradio/ficus), since the kind folks at [iHeartRadio](http://www.iheartradio.com/) have adopted this library.

## Reasons for handing over ownership
I had been neglecting Ficus for several reasons:
* At work we use [knobs](http://oncue.github.io/knobs/)
* I am a bit oversubscribed on open source projects
* Ficus scratched an itch when I first wrote it, but if I were to write it again right now, I would approach it differently.
    * I would represent errors explicitly in the types (currently it throws exceptions, which can be handy, but I would want an alternative).
    * I would use [Shapeless](https://github.com/milessabin/shapeless) to derive readers instead of a macro. At the time, the macro was necessary to use default values on classes, but Shapeless now provides full support for this.
    * I think I would end up writing a `shapeless-knobs` micro library that simply provides Shapeless-derived [Configured](https://github.com/oncue/knobs/blob/master/core/src/main/scala/knobs/Configured.scala) instances.

Having said that, I know there are a number of people that are happily using Ficus. So I'm quite thankful and happy that a team at iHeartRadio has adopted it.
