package com.nitcoders.model;

import java.util.ArrayList;
import java.util.List;

public class Project
{
	private List<String> stimulusTypes;
	private List<Stimulus> stimuli;

	public Project()
	{
		stimulusTypes = new ArrayList<>(List.of(
				"Subject-Object (SO)",
				"Conjoined Clause (CC)",
				"Object-Object (OO)",
				"Object-Subject (OS)",
				"Subject-Subject (SS)"
		));

		stimuli = new ArrayList<>(List.of(
				new Stimulus("The picture that the housekeeper dusted fell off the shelf.", "Subject-Object (SO)", null),
				new Stimulus("The soldier whom the president thanked walked up the stairs.", "Subject-Object (SO)", null),
				new Stimulus("The cookie that the baby grabbed broke into pieces.", "Subject-Object (SO)", null),
				new Stimulus("The student whom the principal scolded told his mother.", "Subject-Object (SO)", null),
				new Stimulus("The computer that the technician rebooted caught on fire.", "Subject-Object (SO)", null),
				new Stimulus("My aunt whom I see rarely came to visit.", "Subject-Object (SO)", null),
				new Stimulus("The banana that I brought to school dropped from my bag.", "Subject-Object (SO)", null),
				new Stimulus("The stars that the sailor saw came out at midnight.", "Subject-Object (SO)", null),
				new Stimulus("The snow that the children shoveled blew across the field.", "Subject-Object (SO)", null),
				new Stimulus("The baby that the mother held drank her bottle.", "Subject-Object (SO)", null),
				new Stimulus("The girl that the man lifted held the basket.", "Subject-Object (SO)", null),
				new Stimulus("The lady that the man touched held the blanket.", "Subject-Object (SO)", null),
				new Stimulus("The bear that the lion followed ate the food.", "Subject-Object (SO)", null),
				new Stimulus("The boy that the girl pushed hugged the cat.", "Subject-Object (SO)", null),
				new Stimulus("The lamb that the goat chased bit the newspaper.", "Subject-Object (SO)", null),
				new Stimulus("The doctor whom the nurse helped checked the baby.", "Subject-Object (SO)", null),
				new Stimulus("The mailman whom the grandmother fed delivered the letter.", "Subject-Object (SO)", null),
				new Stimulus("The sheep that the farmer cleaned drank the water.", "Subject-Object (SO)", null),
				new Stimulus("The cowboy that the sheriff saw rode the train.", "Subject-Object (SO)", null),
				new Stimulus("The teacher that the student greeted bought the book.", "Subject-Object (SO)", null),
				new Stimulus("The plumber that the man hired fixed the pipe.", "Subject-Object (SO)", null),
				new Stimulus("The dragon that the knight fought destroyed the village.", "Subject-Object (SO)", null),
				new Stimulus("The policeman the woman called captured the robber.", "Subject-Object (SO)", null),
				new Stimulus("The artist that the girl admired painted murals.", "Subject-Object (SO)", null),
				new Stimulus("The carpenter hung the picture and fixed the door.", "Conjoined Clause (CC)", null),
				new Stimulus("The president thanked the soldier and walked up the stairs.", "Conjoined Clause (CC)", null),
				new Stimulus("Father drove way too fast and got a ticket.", "Conjoined Clause (CC)", null),
				new Stimulus("The teacher gave out homework and went to lunch.", "Conjoined Clause (CC)", null),
				new Stimulus("The technician rebooted the computer and drank some coffee.", "Conjoined Clause (CC)", null),
				new Stimulus("The banana dropped from my bag and was quickly squashed.", "Conjoined Clause (CC)", null),
				new Stimulus("The sailor saw the stars come out and went to bed.", "Conjoined Clause (CC)", null),
				new Stimulus("The grass got very wet and turned bright green.", "Conjoined Clause (CC)", null),
				new Stimulus("The mother held her baby and talked to the girl.", "Conjoined Clause (CC)", null),
				new Stimulus("The bear chased the dog and wore a hat.", "Conjoined Clause (CC)", null),
				new Stimulus("The man lifted the girl and held a basket.", "Conjoined Clause (CC)", null),
				new Stimulus("The man touched the lady and held a blanket.", "Conjoined Clause (CC)", null),
				new Stimulus("The lion followed the bear and ate the food.", "Conjoined Clause (CC)", null),
				new Stimulus("The girl pushed the boy and hugged a cat.", "Conjoined Clause (CC)", null),
				new Stimulus("The goat chased the lamb and bit a newspaper.", "Conjoined Clause (CC)", null),
				new Stimulus("The grandmother fed the mailman and read the letter.", "Conjoined Clause (CC)", null),
				new Stimulus("The farmer clipped the sheep and drank the water.", "Conjoined Clause (CC)", null),
				new Stimulus("The sheriff pushed the cowboy and rode the train.", "Conjoined Clause (CC)", null),
				new Stimulus("The student greeted the teacher and bought the book.", "Conjoined Clause (CC)", null),
				new Stimulus("The pilot married the actress and bought the plane.", "Conjoined Clause (CC)", null),
				new Stimulus("The man hired the plumber and went to work.", "Conjoined Clause (CC)", null),
				new Stimulus("The knight fought the dragon and saved the village.", "Conjoined Clause (CC)", null),
				new Stimulus("The woman called the policeman and hid under her bed.", "Conjoined Clause (CC)", null),
				new Stimulus("The cat ate the mouse and played with yarn.", "Conjoined Clause (CC)", null),
				new Stimulus("The artist painted murals and built some sculptures.", "Conjoined Clause (CC)", null),
				new Stimulus("The secretary satat the desk that was painted red.", "Object-Object (OO)", null),
				new Stimulus("I saw the tree where the eagles built their nest.", "Object-Object (OO)", null),
				new Stimulus("The baby grabbed the cookie that the ants were on.", "Object-Object (OO)", null),
				new Stimulus("The driver went down the street that I live on.", "Object-Object (OO)", null),
				new Stimulus("The clown jumped in the car that the monkeys were pounding.", "Object-Object (OO)", null),
				new Stimulus("The dancers wore the shoes that the director brought.", "Object-Object (OO)", null),
				new Stimulus("The house was on the hill where lightning struck.", "Object-Object (OO)", null),
				new Stimulus("The baker showed the cake that the children had eaten.", "Object-Object (OO)", null),
				new Stimulus("The janitor picked up the apple that rolled across the floor.", "Object-Subject (OS)", null),
				new Stimulus("The housekeeper dusted the picture that fell off the shelf.", "Object-Subject (OS)", null),
				new Stimulus("The children shoveled the sand that was near the ocean.", "Object-Subject (OS)", null),
				new Stimulus("The birds ate the worms that crawled on the driveway.", "Object-Subject (OS)", null),
				new Stimulus("Sharks bite swimmers who float in the ocean.", "Object-Subject (OS)", null),
				new Stimulus("Daddy hugged the child who made the birthday cake.", "Object-Subject (OS)", null),
				new Stimulus("The singer thanked the crowd that came to see him.", "Object-Subject (OS)", null),
				new Stimulus("The lady made the sign that said \"free kittens.\"", "Object-Subject (OS)", null),
				new Stimulus("The girl who cleaned the kitchen ate some lunch.", "Subject-Subject (SS)", null),
				new Stimulus("The principal who thanked the student walked outside.", "Subject-Subject (SS)", null),
				new Stimulus("The man who opened the grocery store made the coffee.", "Subject-Subject (SS)", null),
				new Stimulus("The grass that grew in his yard got very wet.", "Subject-Subject (SS)", null),
				new Stimulus("The man that held a blanket touched the lady.", "Subject-Subject (SS)", null),
				new Stimulus("The lion that ate the food followed the bear.", "Subject-Subject (SS)", null),
				new Stimulus("The goat that bit a newspaper chased the lamb.", "Subject-Subject (SS)", null),
				new Stimulus("The nurse that checked the baby helped the doctor.", "Subject-Subject (SS)", null)
		));
	}

	public List<Stimulus> getStimuli()
	{
		return stimuli;
	}

	public List<String> getStimulusTypes()
	{
		return stimulusTypes;
	}
}
