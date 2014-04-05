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
	jumpStrength: 500,	//Force upplied for jump from the ground
	groundSpeed: 20,	//Speed when moving on the ground
	airSpeed: 5,		//Speed when in the air
	friction: .1,		//Firction on the b2Body
	pushStrength: 100,	//Strength when jumping off a wall
	wallNormal: null,	//The normal of the wall we are stuck to
	jumpCharge: 0, 		//Multiplies jumpStrength  or pushStrength (ground vs wall) (Because the player can charge their jump, duh)
	jumpMaxCharge: 3,   //jumpCharge can't go higher than this

	//Buttons toggles used for timing.
	jumpCharging: false,
	
	init: function( x, y, settings ) {
		this.parent( x, y, settings );
		
		// Add the animations
		this.addAnim( 'idle', 1, [0] );

		if(!ig.global.wm) {
			this.body.SetFixedRotation(true);
		}

		this.body.SetUserData(this)
	},
	
	
	update: function() {
		//Move left on ground
		if( ig.input.state('left') ) {
			var speed = (this.onGround) ? this.groundSpeed : this.airSpeed;

			if(this.body.GetLinearVelocity().Length() < speed){
				this.body.ApplyForce( new Box2D.Common.Math.b2Vec2(-speed,0), this.body.GetPosition() );
				this.flip = true;
			}
		
		//Move right on ground
		}else if( ig.input.state('right') ) {
			var speed = (this.onGround) ? this.groundSpeed : this.airSpeed;

			if(this.body.GetLinearVelocity().Length() < speed){
				this.body.ApplyForce( new Box2D.Common.Math.b2Vec2(20,0), this.body.GetPosition() );
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
		if( ig.input.state('jump') && (this.onGround || this.onWall)) {
			this.chargeJump()

		//Release ground jump
		} else if( !ig.input.state('jump') && this.onGround && this.jumpCharging ) {			
			this.onGround = false;

			//Jump
			this.body.ApplyForce( new Box2D.Common.Math.b2Vec2(0,-this.jumpStrength * this.jumpCharge), this.body.GetPosition() );
			this.currentAnim = this.anims.idle;

			//Reset
			this.resetJump()

		//Release wall jump
		}else if(!ig.input.state('jump') && this.onWall && this.jumpCharging){
			//Calculate push
			var push = this.wallNormal.GetNegative()
			push.Add(new Box2D.Common.Math.b2Vec2(0, -2))
			push.Multiply(this.pushStrength * this.jumpCharge)
             
			//Push
			this.body.SetActive(true);
			this.body.ApplyForce( push, this.body.GetPosition() );

			//Reset
			this.resetJump()
			this.onWall = false

		//Floating
		}else{
			this.currentAnim = this.anims.idle;
		}
		
		// shoot
		if( ig.input.pressed('shoot') ) {
			var x = this.pos.x + (this.flip ? -6 : 6 );
			var y = this.pos.y + 6;
			ig.game.spawnEntity( EntityProjectile, x, y, {flip:this.flip} );
		}
		
		this.currentAnim.flip.x = this.flip;

		//Grab walls
		if(this.onWall && this.body.IsActive() && !this.onGround){
			this.body.SetActive(false)
		}
		
		// move!
		this.parent();
	},

	chargeJump: function(){
		this.jumpCharging = true
		this.jumpCharge += 1 * ig.system.tick
	},

	resetJump: function(){
		this.jumpCharging = false
		this.jumpCharge = 1;
	},

	collidedWithWall: function(wall, contact, impulse){
		var mani = contact.GetManifold();
		this.wallNormal = mani.m_localPlaneNormal ;
		if(this.wallNormal.y == 1){
			this.onGround = true
		}else if(this.wallNormal.y != -1){
			this.onWall = true
		}
	}
});


EntityProjectile = ig.Box2DEntity.extend({
	size: {x: 8, y: 4},
	
	type: ig.Entity.TYPE.NONE,
	checkAgainst: ig.Entity.TYPE.B, 
	collides: ig.Entity.COLLIDES.NEVER, // Collision is already handled by Box2D!
		
	animSheet: new ig.AnimationSheet( 'media/projectile.png', 8, 4 ),	
	
	init: function( x, y, settings ) {
		this.parent( x, y, settings );
		
		this.addAnim( 'idle', 1, [0] );
		this.currentAnim.flip.x = settings.flip;
		
		var velocity = (settings.flip ? -10 : 10);
		this.body.ApplyImpulse( new Box2D.Common.Math.b2Vec2(velocity,0), this.body.GetPosition() );
	}	
});

});