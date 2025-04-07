# What is GIT used for?

In this course, unless otherwise explicitly stated, your exercises will be submitted via GitLab. The last commit pushed before the deadline to the master branch on GitLab is decisive for the evaluation, grading, and feedback. The feedback will be provided here, i.e., on GitLab in a **DESIGN or FINAL milestone** respectively. To access these milestones check out the toolbar entry on the left named “Tickets” in German or “Issues” in English. The (preliminary) achieved points will be published using Moodle. 

**Do not change the name of the master branch**: it must be called master. Only data that is in the master branch before the deadline (therefore committed and pushed before the deadline) will be taken into account during the submission interviews and evaluation. Double check that you use the correct repository (i.e., the one relevant for this semester and this course). 

# How do I get local access to this repository?

To work optimally with this repository, you should mirror it to your local workstation. To do this, use the `git clone <yourRepoUrl>` command. To get hold of the required repository URL scroll up till you see a blue button labeled Clone - click it. Select the URL provided by “Clone with HTTPS”. This should be similar to https://git01lab.cs.univie.ac.at/......

An alternative would be “Clone with SSH”. We typically only recommend it if you have already a bit of experience with Git and SSH. For example, because this would require you to create public and private keys for authentication reasons.  

**Problems with the certificates**: If you are experiencing problems cloning your Git repository and you are experiencing problems with certificate validation, a quick solution is to turn it off (as a last resort). You can use the following command: git config --global http.sslVerify false

# How do I use this repository?

Clone this repository as indicated above. Then you can interact with it based on standard git commands, such as, `git add`, `commit`, `push`, `checkout` etc. To do so you will need to specify your name and email address after the initial clone. This information is subsequently automatically used during each commit. Use the following commands to do so:

> `git config --global user.name "My name"`

> `git config --global user.email a123456@univie.ac.at`

Use your **real name** (i.e., not a nickname or an abbreviation) and your official **university mail address** (mandatory). Further, we recommend that you and your team members organize their efforts (e.g., work packages and their assignment) but also critical communication (e.g., if team members are unresponsive) here in GitLab using Git issues.

# How are questions handled?

For general inquiries (which are relevant for multiple teams) please use the [Moodle Disscussion Forum - Practical Assignment](https://moodle.univie.ac.at/mod/forum/view.php?id=16414102). In case of individual inquiries, please contact our **tutors first** either via ase.tutor@swa.univie.ac.at or GitLab handle
- Robert Sama (git handle @roberts95) 
- Samuel Mitterrutzner (git handle @samuelm00) 

If they is unable to provide assistance, they will forward your inquiry to the relevant supervisor. 

For exceptional situations, e.g., team building issues, you can also contact your supervisor directly. Based on experience, most Git issues are not related to us but are focused on internal team communications. Hence, if you want to contact a tutor or supervisor, always use their Git handles in your issues such that the respective person is notified about your inquiry by GitLab.

Your **team supervisor** can be identified based on your team’s id. 
- For teams `010X` the supervisor is Pierre-Jean Quéval (git handle @quevalp84). 
- For teams `020X` the supervisor is Nicole Lueger (git handle @luegern93). 
- For teams `030X` the supervisor is Amirali Amiri (git handle @amiralia57).
- For teams `040X` the supervisor is Amine El Malki (git handle @elmalkia29).
- For teams `050X` the supervisor is Stephen Warnett (git handle @stephenw64).
- For teams `060X` the supervisor is Maximilian Wöhrer (git handle @woehrea2).

Use these git handles to specifically address a person in a Git issue. Please **do not use** @all as this would notify all supervisors, use the specific handle of your supervisor listed above. As a last resort, you can contact the course [email](mailto:ase@swa.univie.ac.at). 

# Which functions should not be used?

GitLab is a powerful software that allows you to customize numerous settings and choose from various features. We would advise you to treat these settings and features with **respect and care**. For example, by simply clicking on each button, ignoring warnings etc. one could delete this repositorie’s master branch for good (no we can’t restore it eather). So: Think before you click!

