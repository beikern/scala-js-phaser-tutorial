package example

import com.definitelyscala.phaser.Physics.Arcade.Body
import com.definitelyscala.phaser._
import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.util.Random

object ScalaJSExample extends js.JSApp {
  def main(): Unit = {
    dom.document.getElementById("scalajsShoutOut").textContent = "Scala.js is up & running!"
    new TutorialPhaserIo
  }
}

class TutorialPhaserIo {

  val game = new Game(800, 600, Phaser.AUTO, "", State(preload _, create _, update _))

  var player: Sprite = _

  var platforms: Group = _
  var stars: Group = _

  var cursors: CursorKeys = _
  var spacebarKey: Key = _

  var score: Int = 0
  var scoreText: Text = _

  def preload(): Unit = {
    game.load.image("logo", "assets/images/phaser.png")
    game.load.image("sky", "assets/images/sky.png")
    game.load.image("ground", "assets/images/platform.png")
    game.load.image("star", "assets/images/star.png")
    game.load.spritesheet("dude", "assets/images/dude.png", 32, 48)
  }

  def create(): Unit = {
    game.physics.startSystem(PhysicsObj.ARCADE)
    game.add.sprite(0, 0, "sky")

    // platform group defined, used for collision testing, add here the floor and ledges
    platforms = game.add.group()
    platforms.enableBody = true

    // declaring ground, scaling it and making immovable, because it's a floor :)
    val ground= platforms.create(0, game.world.height - 64, "ground").asInstanceOf[Sprite]

    ground.scale.set(2,2)

    ground.body match {
      case body: Body =>
        body.immovable = true
    }

    // Same as ground
    val ledge1 = platforms.create(400, 400, "ground")
    ledge1.body.immovable = true
    val ledge2 = platforms.create(-150, 250, "ground")
    ledge2.body.immovable = true

    // Dude is now alive!
    player = game.add.sprite(45, game.world.height-600, "dude")

    // Adding physics to our dude
    game.physics.arcade.enable(player)

    player.body match {
      case body: Body =>
        body.bounce.y = 0.3
        body.gravity.y = 800
        body.collideWorldBounds = true
    }

    // Adding animations to our dude
    player.animations.add("left",Array[Double](0,1,2,3).toJSArray, 10, true)
    player.animations.add("right",Array[Double](5,6,7,8).toJSArray,10, true)

    // Declaring the keys to move our dude
    spacebarKey = game.input.keyboard.addKey(KeyCode.SPACEBAR)
    game.input.keyboard.addKeyCapture(KeyCode.SPACEBAR)
    cursors = game.input.keyboard.createCursorKeys()

    // Creating starts and giving them a bit of life
    stars = game.add.group()

    stars.enableBody = true

    List.range(1, 12).foreach {
      xpos =>
        val star = stars.create(xpos * 70, 0, "star").asInstanceOf[Sprite]
        star.body match {
          case body: Body =>
            body.gravity.y = 55
            body.bounce.y = 0.7 + new Random().nextDouble() * 0.2
        }

    }

    // Creating the score
    scoreText = game.add.text(16, 16, "score: 0", ScoreStyle("32px", "#000"), game.world)
  }

  val collectStar: js.Function2[Sprite, Sprite, Sprite] =
    (player: Sprite, star: Sprite) => {
    score += 10
    scoreText.text = s"Score: $score"
    star.kill()
  }

  def update(): Unit = {

    // Physics
    game.physics.arcade.collide(player, platforms)
    game.physics.arcade.collide(stars, platforms)
    game.physics.arcade.overlap(player, stars, collectStar, null)

    // Movement, gravity, animations
    player.body match {
      case body: Body =>
        if (cursors.left.isDown) {
          body.velocity.x = -150
          player.animations.play("left")
        } else if (cursors.right.isDown) {
          body.velocity.x = 150
          player.animations.play("right")
        } else {
          player.animations.stop()
          body.velocity.x = 0
          player.frame = 4
        }
        if (spacebarKey.isDown && body.touching.down) {
          body.velocity.y = -550
        }
    }
  }
}

object ScoreStyle {
  def apply(fontSize: String, fill: String): ScoreStyle = {
    js.Dynamic.literal(fontSize = fontSize, fill = fill).asInstanceOf[ScoreStyle]
  }
}

@js.native
trait ScoreStyle extends js.Object {
  val fontSize: String = js.native
  val fill: String = js.native
}

// This state is the way scala.js models a Javascript literal object -> https://www.w3schools.com/js/js_objects.asp
object State {
  def apply(preload: () => Unit,  create: () => Unit, update: () => Unit): State = {
    js.Dynamic.literal(preload = preload, create = create, update = update).asInstanceOf[State]
  }
}

@js.native
trait State extends js.Object {
  def preload: Unit = js.native
  def create: Unit = js.native
  def update: Unit = js.native
}