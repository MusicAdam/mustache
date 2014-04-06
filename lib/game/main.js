ig.module( 
	'game.main' 
)
.requires(
	'impact.game',
	'impact.font',
	
	'game.entities.player',

	'game.levels.test',
	
	'plugins.box2d.game',
    'impact.debug.debug'
)
.defines(function(){

var b2ContactListener = Box2D.Dynamics.b2ContactListener;

MyGame = ig.Box2DGame.extend({
	
	gravity: 100, // All entities are affected by this
	
	// Load a font
	font: new ig.Font( 'media/04b03.font.png' ),
	clearColor: '#1b2026',
	
	init: function() {
		ig.PLAYER_ENTITY = ig.Entity.TYPE.A
		ig.BULLET_ENTITY = ig.Entity.TYPE.B

		var listener = new b2ContactListener;
		var game = this;

		listener.BeginContact = function(contact, impulse){
			var data = game._parseContactData(contact);

			//Player wall collision
			if(data.player != null){
				if(data.wall != null){
					data.player.collidedWithWall(data.wall, contact, impulse);
				}
			}

			//Bullet wall collision
			if(data.bullet != null){
				if(data.wall != null){
					data.bullet.kill()
				}
			}
		}

		listener.EndContact = function(contact, impulse){
			var data = game._parseContactData(contact);

			if(data.player != null){
				if(data.wall != null){
					data.player.endContactWithWall(data.wall, contact, impulse);
				}
			}			
		}

		// Bind keys
		ig.input.bind( ig.KEY.A, 'left' );
		ig.input.bind( ig.KEY.D, 'right' );
		ig.input.bind( ig.KEY.SPACE, 'jump' );
		ig.input.bind( ig.KEY.W, 'charge' );
		ig.input.bind( ig.KEY.MOUSE1, 'shoot' );
		
		// Load the LevelTest as required above ('game.level.test')
		this.loadLevel( LevelTest );
		ig.world.SetContactListener(listener)
	},

	//Takes contact and returns an object containg:
	//	{name_of_entity_a: entity_a, name_of_entity_b: entity_b}
	_parseContactData: function(contact){
		var bodyA = contact.GetFixtureA().GetBody()
		var bodyB = contact.GetFixtureB().GetBody()
		var userDataA = this._parseContactUserData(bodyA.GetUserData());
		var userDataB = this._parseContactUserData(bodyB.GetUserData());
		var data = {}

		if(bodyA.GetType() == Box2D.Dynamics.b2Body.b2_staticBody){
			data.wall = bodyA
		}

		if(bodyB.GetType() == Box2D.Dynamics.b2Body.b2_staticBody){
			data.wall = bodyB
		}

		if(userDataA.player != null){
			data.player = userDataA.player
		}

		if(userDataB.player != null){
			data.player = userDataB.player
		}

		if(userDataA.bullet != null){
			data.bullet = userDataA.bullet
		}

		if(userDataB.bullet != null){
			data.bullet = userDataB.bullet
		}


		return data;
	},

	//Helper for _parseContactData, decides what type of entity the userdata is
	_parseContactUserData: function(userData){
		data = {}
		if(userData != null){
			if(userData.type == ig.PLAYER_ENTITY){
				data.player = userData
			}else if(userData.type == ig.BULLET_ENTITY){
				data.bullet = userData
			}else{
				//unknwon entity type
				console.log("b2ContactListener: Detected unknwon collision with entity of type " + userData.type)
			}
		}

		return data;
	},
	
	loadLevel: function( data ) {
		this.parent( data );
		for( var i = 0; i < this.backgroundMaps.length; i++ ) {
			this.backgroundMaps[i].preRender = true;
		}
	},
	
	update: function() {
		// Update all entities and BackgroundMaps
		this.parent();
		
		// screen follows the player
		var player = this.getEntitiesByType( EntityPlayer )[0];
		if( player ) {
			this.screen.x = player.pos.x - ig.system.width/2;
			this.screen.y = player.pos.y - ig.system.height/2;
		}
	},
	
	draw: function() {
		// Draw all entities and BackgroundMaps
		this.parent();
	}
});

ig.main('#canvas', MyGame, 60, 600, 600, 2);



});
