Tetra-Runner
============

An original 3D Android game using OpenGL ES. 
Created initially as a final project for the mobile development class at the University of Utah, 2014.
Total development time upon first release: 53 hours.

The concept:
============

I wanted to make a game using the now-established "runner" genre that has gained so much popularity 
in the mobile gaming market. Analyzing such popular games as Temple Run, Robot Unicorn Attack, etc,
I knew that the game thrives on having simple rules and a simple control scheme, but also a unique style.
I had also learned quite a great deal about 3D graphics, so I thought this would be a great opportunity
to demonstrate that knowledge.

As a result, Tetra Runner was born. The game uses very simple geometry and a control scheme consisting 
only of three different inputs: move left, move right, and jump. In game, the only things to do are avoid
walls, jump over holes, and collect tetrahedron (shortened only to "tetra" in-game).

Creating this game was a challenging learning experience, but also incredibly fun. I had to do a lot of
learning about OpenGL ES, and about game programming in general. I'd never really made a full game from
scratch all on my own, so a lot of the effort spent on this project went into just the architecture 
and design of the thing.


What skills this project demonstrates:
============

  -Knowledge of Android API; makes use of fragments, multiple activities, list adapters, etc.
  -Use of smooth interactive 3D graphics with phong lighting
  -Game logic; uses a game loop, keeps track of high scores, collision detection, win/loss conditions, etc.
  -Strong object-oriented programming practice
  -Ability to conceptualize, design, and execute a project from start to finish.
  -Aesthetic design; all assets, visual and audio, are completely original.
  -Ability to work with a tight deadline. Was given two weeks to complete; stayed on schedule while also
      managing other school projects.



Future plans for this project:
============

  *Optimization.*
This project is not incredibly efficient. Even if the game is very simple, there is still much I don't
know about optimizing 3D graphics with OpenGL, but even still, there are very specific things I know I
can improve. As far as future plans go, this one is the number one priority.

  *More stages.*
Obviously, this game is incredibly thin on content. Admittedly, this was more or less a demonstration of 
technical knowledge than anything else, but it's also kinda fun. In the future, though, I want to have
hundreds of levels, all of varying difficulty. I think a reasonable number to shoot for is ten of each
difficulty level (easy, moderate, hard, extreme), in order to call this game a full fledged "game".

  *More gameplay elements.*
What I mean by this is for more in-game objects to interact with. More obstacles, power-ups, stuff like that.
Some ideas I had were having 8 red tetrahedron to collect within each stage, so each stage would have another
objective to achieve. Another idea was for having consumable boosts, maybe 3, the player could use whenever
they felt appropriate, giving them a burst of speed for a short duration (a second or two). This would create
more variety between each playthrough of stages, allowing for more opportunity to crack the best times. Also
wanted to have more interesting obstacles; things like some sort of "bomb" that explodes and sends you flying
in a direction when you touch it, or perhaps some spots on the track that either speed you up or slow you down
when you drive over them, or even some ramps that launch you into the air when you drive over them. These would
be pretty simple to implement and would add a great deal of depth to the game.

  *More modes of play.*
While having high scores and best times for each individual level is great and a solid foundation on which
to build a game, I think this game needs more. One mode I'd really like to create is an "endless" mode, where
the player is sent into an endless procedurally-generated track, and to see how long they can go for. Ideally
the track would get harder as the game progresses. Another mode I'd like to create is some sort of level
editor, where users can create their own tracks and share with other players. This idea is quite a bit more
involved, and would require not only creating the editor, but also some method of sharing these levels.

  *Updated menus.*
This should probably be a higher priority. The menu is kind of ugly as it is right now, and also somewhat
unintuitive. One idea I had would be to integrate the high scores and best times directly into the stage
select screen (ie. selecting a stage would display the best time/score, and a button to play the stage or
view detailed records). I also want to create more options for the user; maybe for some graphical options, to
change the lighting to favor performance, or to modify the control scheme somehow. The menus could also favor
from some better layouts, a better font choice, better colors, possibly some animation. You know, aesthetic
stuff.

  *Updated visuals.*
While I like the visual style as it is, I think there could be more done. Some texturing, more ship models,
some backgrounds, different lighting effects, stuff like that. As is, it looks cool, but it's also somewhat
plain as there is little variety.

  *More sound/music.*
As of this writing, there is exactly one song and three sound effects. This is a bit meager, and more should
be added to what I'd consider a finished product. A victory and death sound should definitely be added. Perhaps
music/sounds in the menus would be appropriate as well. 



Some notable challenges I had to overcome:
============

  *Performance.*
I'd never made a fully interactive 3D project like this before, let alone one for a mobile
device. I'm used to programming applications on larger machines, where computing power and efficiency are
*almost* something that can be taken for granted, but of course, are still something I consider when 
writing algorithms and the like. But with this project, for my first time maybe ever, performance concerns 
had to be made a priority. On some stages with lots of objects, there is still a noticeable framerate drop;
this is due to my quick-and-dirty implementation of things, which puts way too much unnecessary strain on the
processor, entirely to get things up and running. I plan to go back and restructure things in a proper 
manner, but for this project, I was on a pretty tight time-table.

  *Collision detection.*
Like I mentioned before, I'd never made a game on my own of this calibre, apart from
simple text-based games like Blackjack, or a simple tower-defense game made under the guidance of a former
professor. So collision detection -- and really the overall physics of a game -- were leaps of faith for
me. I spent hours tweaking things to get them right, and even still, they're a bit clunky, but work well 
enough. Issues arise when the framerate drops, for reasons mentioned in the above paragraph, and can be
seen on certain levels included in the game currently (specifically, "Garbage Alley").

  *HUD displays.*
While I had worked with textures and understood how they worked, getting a HUD to display took much more
time than I anticipated. First of all, I had to learn how to display text in OpenGL, and the fastest way 
to do so seems to be by mapping images of letters/numbers as textures to squares on the screen. This is
all fine and good, but I ran into a few issues, and it wound up taking more than eight hours of time just
to get a single number displayed on screen. The main reason was because I had never actually used multiple 
shaders in the same project before, so I had to learn the proper way to do that (in the end, it's simply 
a matter of linking both shaders, and then switching between programs, not a big deal). The second issue
was figuring out why my textures simply would not display. I had followed each step appropriately; loaded
the bitmap, set the filtering, mapped to geometry... but the display wouldn't show. And while there are 
plenty of helpful Stack Overflow pages that answer similar questions, it seemed none were quite at my level 
of understanding. I spent hours just reading, trying to get this to work. Finally, through process of
elimination, I discovered the back-face culling option that was enabled for the rest of the game was making
my textures not show up; this was alleviated by simply disabling and re-enabling the culling as needed.
Hours upon hours wasted, just on this one simple little thing!


Credits:
============

Logo designed by Deb Newey, @_thefickleartist_ on instagram.
Music made in collaboration with Matone, https://soundcloud.com/matone
