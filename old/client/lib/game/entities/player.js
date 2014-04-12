ig.module(
	'game.entities.player'
)
.requires(
	'impact.entity',
	'plugins.box2d.entity'
)
.defines(function(){

EntityPlayer = ig.Box2DEntity.extend({
	size: {x: 8, y:14},
	//offset: {x: 4, y: 2},
	
	type: ig.Entity.TYPE.A,
	checkAgainst: ig.Entity.TYPE.NONE,
	collides: ig.Entity.COLLIDES.NEVER, // Collision is already handled by Box2D!
	
	animSheet: new ig.AnimationSheet( 'media/player.png', 8, 14 ),	
	
	flip: false,
	onGround: false,
	onWall: false,	
	jumpStrength: 800,	//Force upplied for jump from the ground
	groundSpeed: 20,	//Speed when moving on the ground
	airSpeed: 10,		//Speed when in the air
	friction: .1,		//Firction on the b2Body
	pushStrength: 300,	//Strength when jumping off a wall
	wallNormal: null,	//The normal of the wall we have last touched
	jumpCharge: 1, 		//Multiplies jumpStrength  or pushStrength (ground vs wall) (Because the player can charge their jump, duh)
	jumpMaxCharge: 1.8,   //jumpCharge can't go higher than this
	mouseDirection: null,
	//Buttons toggles used for timing.
	jumpCharging: false,
	
	init: function( x, y, settings ) {
		this.parent( x, y, settings );
		
		// Add the animations
		this.addAnim( 'idle', 1, [0] );

		if(!ig.global.wm) {
			this.body.SetFixedRotation(true);
			this.body.SetUserData(this)

			this.light = ig.game.lightManager.addLight(this, {
            angle: 0,   
            angleSpread: 365,   
            radius: 500,         
            color:'rgba(255,255,255,0.1)',  // there is an extra shadowColor option
            useGradients: true, // false will use color/ shadowColor
            shadowGradientStart: 'rgba(0,0,0,0.1)',         // 2-stop radial gradient at 0.0 and 1.0
            shadowGradientStop: 'rgba(0,0,0,0.1)',
            lightGradientStart: 'rgba(255,255,100,0.1)',    // 2-stop radial gradient at 0.0 and 1.0
            lightGradientStop: 'rgba(0,0,0,0.6)',
            lightOffset: {x:0,y:0}      // lightsource offset from the middle of the entity
        });
		}

		
	},
	
	
	update: function() {
		//control body flip according to mouse direction
		this.mouseDirection = this.calcMouseDirection()
		if(this.mouseDirection.x < 0){
			this.flip = true;			
		}else{
			this.flip = false;
		}

		//Move left on ground
		if( ig.input.state('left') ) {
			var speed = (this.onGround) ? this.groundSpeed : this.airSpeed;

			if(this.jumpCharging){
				speed = speed/2
			}

			if(this.body.GetLinearVelocity().Length() < speed){
				this.body.ApplyForce( new Box2D.Common.Math.b2Vec2(-speed,0), this.body.GetPosition() );
			}
		
		//Move right on ground
		}else if( ig.input.state('right') ) {
			var speed = (this.onGround) ? this.groundSpeed : this.airSpeed;

			if(this.jumpCharging){
				speed = speed/2
			}

			if(this.body.GetLinearVelocity().Length() < speed){
				this.body.ApplyForce( new Box2D.Common.Math.b2Vec2(speed,0), this.body.GetPosition() );
				this.flip = false;
			}

		//Slow on ground
		}else if(this.onGround){ 
			var vel = this.body.GetLinearVelocity().GetNegative()
			if(vel.Length() > .1){
				vel.Multiply(350 * ig.system.tick);
				this.body.ApplyForce( vel, this.body.GetPosition() );
			}
		}
		
		//Charge jump
		if( ig.input.state('charge') && (this.onGround || this.onWall) ) {
			this.chargeJump()

		//cancel charge
		}else if( !ig.input.state('charge') && this.jumpCharging){
			this.resetJump()
		}

		//Release ground jump
		if( ig.input.state('jump') ) {	
			this.jump();

		}
		
		// shoot
		if( ig.input.pressed('shoot') ) {
			var dir = (this.flip ? -1 : 1 );

			//Adjust where the projectile spawns so its not inside the player
			//TODO: Spawn from EntitySpawner which will be placed on the player and toggled with this.flip
			var spawnPos = new Box2D.Common.Math.b2Vec2(this.pos.x, this.pos.y)
				spawnPos.x = spawnPos.x + (this.mouseDirection.x * (this.size.x ))
				spawnPos.y = spawnPos.y + (this.mouseDirection.y * (this.size.y))
			ig.game.spawnEntity( EntityProjectile, spawnPos.x, spawnPos.y, {flip: this.flip, direction: this.mouseDirection} );
		}
		

		//Grab walls
		if(this.onWall && this.body.IsActive() && !this.onGround && this.shouldStickToWall()){
			this.body.SetActive(false)
		}
	
		this.currentAnim.flip.x = this.flip;
		this.parent();

		ig.show('jumpCharge', this.jumpCharge)
		ig.show('onGround', this.onGround)
		ig.show('onWall', this.onWall)
		if(this.wallNormal != null)
			ig.show('wallNormal', this.wallNormal.x)
	},

	calcMouseDirection: function(){
        var click = this.ClickCoordinates(ig.input.mouse.x,
                                          ig.input.mouse.y);
		var direction = new Box2D.Common.Math.b2Vec2(click.x - this.body.GetPosition().x, click.y - this.body.GetPosition().y)
		direction.Normalize()

		return direction//new Box2D.Common.Math.b2Vec2(1, 1)
	},

	shouldStickToWall: function(){
		if(this.wallNormal != null){
			if(this.wallNormal.x == -1){
				return ig.input.pressed("left")
			}else if(this.wallNormal.x == 1){
				return ig.input.pressed("right")
			}
		}

		return false
	},

	jump: function(){
		if(!this.onGround && !this.onWall) return;

		var jumpDirection, jumpStrength;

		//Setup ground jump
		if( this.onGround ) {
			jumpDirection = new Box2D.Common.Math.b2Vec2(0, -1)
			jumpStrength = this.jumpStrength
			this.onGround = false;

		//Setup wall jump
		}else if( this.onWall ){
			jumpStrength = this.pushStrength
			jumpDirection = this.wallNormal.GetNegative()
			jumpDirection.Add(new Box2D.Common.Math.b2Vec2(0, -2))
		}

		var mouseDirection = this.mouseDirection
			mouseDirection.Multiply(this.jumpCharge)

		jumpDirection.Add(mouseDirection)
		jumpDirection.Normalize()
		jumpDirection.Multiply(jumpStrength + (jumpStrength * this.jumpCharge) )


		//Jump
		this.body.SetActive(true);
		this.body.ApplyForce(jumpDirection, this.body.GetPosition())

		//Reset
		this.resetJump()
	},

	chargeJump: function(){
		this.jumpCharging = true
		if(this.jumpCharge < this.jumpMaxCharge)
			this.jumpCharge += 1 * ig.system.tick
	},

	resetJump: function(){
		this.onGround = false;
		this.onWall = false;
		this.wallNormal = null
		this.jumpCharging = false
		this.jumpCharge = 0;
	},

	collidedWithWall: function(wall, contact, impulse){
		var mani = contact.GetManifold();
		this.wallNormal = mani.m_localPlaneNormal ;
		if(this.wallNormal.y == 1){
			this.onGround = true
		}else if(this.wallNormal.y != -1 && !this.onGround){
			this.onWall = true
		}
	},

	endContactWithWall: function(wall, contact, impule){
		if(this.wallNormal == null) return

		//Reset onGround when player walks off a ledge
		if(this.wallNormal.y == 1){
			this.onGround = false
		}
	},

	ClickCoordinates: function(localX, localY) {
        return { x: (localX + ig.game.screen.x) * Box2D.SCALE,
                 y: (localY + ig.game.screen.y) * Box2D.SCALE };
    }
});

if(!ig.global.wm){
	EntityProjectile = ig.Box2DEntity.extend({
		size: {x: 8, y: 4},
		
		type: ig.Entity.TYPE.B,
		checkAgainst: ig.Entity.TYPE.NONE, 
		collides: ig.Entity.COLLIDES.NEVER, // Collision is already handled by Box2D!
			
		animSheet: new ig.AnimationSheet( 'media/projectile.png', 8, 4 ),	
		
		init: function( x, y, settings ) {
			this.parent( x, y, settings );
			
			this.addAnim( 'idle', 1, [0] );
			this.currentAnim.flip.x = settings.flip;
			this.direction = settings.direction
			this.body.SetBullet(true);
			this.body.SetUserData(this)

			var velocity = 25
			this.direction.Multiply(velocity)
			this.body.ApplyImpulse( this.direction, this.body.GetPosition() );
		}	
	});

	EntityProjectileSpawner = ig.Box2DEntity.extend({

	})
}

});