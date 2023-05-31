package org.lolicode.nekomusiccli.libs.lrcparser;

import org.lolicode.nekomusiccli.libs.lrcparser.parser.Sentence;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

public class Lyric{

	/**
	 * ID Tags [by:Creator of the LRC file] the key is "by" and the value "Creator of the LRC file"
	 */
	private Hashtable<String,String> tags;
	/**
	 * a list of Sentence which presents each line of LRC file
	 */
	private ArrayList<Sentence> sentences;

	/**
	 * total duration of the lyric
	 */
	private long duration;

	
	public Lyric(Hashtable<String,String> tags,ArrayList<Sentence> sentences){
		super();
		this.tags=tags;
		this.sentences=sentences;
		this.updateDuration();
	}
	

	public static String[] findContents(List<Sentence> sentences){
		String[] contents=new String[sentences.size()];
		for(int i=0;i<contents.length;i++){
			contents[i]=sentences.get(i).getContent();
		}
		return contents;
	}

	/**
	 * list all lyric between <code>fromTime</code> to <code>toTime</code>
	 * 
	 * @param fromTime
	 * @param toTime
	 * @return
	 */
	public String[] findAllContents(long fromTime,long toTime){
		return findContents(findAllSentences(fromTime,toTime));
	}

	/**
	 * list all lyric.
	 * 
	 * @return
	 */
	public String[] findAllContents(){
		return findAllContents(-1,-1);
	}

	/**
	 * find Sentence correspondant the line in lyric correspondant at a specific <code>time</code>
	 * <p/>
	 * <strong>NOTE:</strong> if there are more than one Sentence at the same time, only the first one will be returned.
	 * <strong>NOTE:</strong> This method has been modified for the purpose of this project.
	 * It now returns the sentence whose fromTime is <strong>before</strong> the given time and whose toTime is <strong>after</strong> the given time.
	 * 
	 * @param time
	 * @return
	 */
	public Sentence findSentence(long time){
		if (time<0)
			throw new RuntimeException("time<0");
		for (Sentence sentence : sentences) {
			if (sentence.getFromTime() <= time && sentence.getToTime() >= time) {
				return sentence;
			}
		}
		return null;
	}

	/**
	 * find content of the line in lyric correspondant at a specific time
	 * 
	 * @param time
	 * @return
	 */
	public String findContent(long time){
		Sentence sent=findSentence(time);
		if (sent==null)
			return null;
		return sent.getContent();
	}

	/**
	 * getAllSentenes between fromTime and toTime
	 * 
	 * @param fromTime
	 *            -1 means no condition limited
	 * @param toTime
	 *            -1 means no condition limited
	 * @return List<Sentence>
	 */
	public ArrayList<Sentence> findAllSentences(long fromTime,long toTime){
		boolean begin=false;
		ArrayList<Sentence> result=new ArrayList<Sentence>(sentences.size()/3);
		if (fromTime>=0&&toTime>=0&&fromTime>toTime){// fromTime>toTime
			return result;
		}
		for(Sentence sent:sentences){
			if (!begin){
				if (sent.getFromTime()>=fromTime){
					begin=true;
					result.add(sent);
				}
			}else{
				if (toTime>=0&&sent.getFromTime()>toTime){
					break;
				}
				result.add(sent);
			}
		}
		return result;
	}

	public ArrayList<Sentence> getSentences(){
		return sentences;
	}

	public Hashtable<String,String> getTags(){
		return tags;
	}
	
	public boolean isEmpty(){
		return sentences.isEmpty();
	}
	
	public long getDuration(){
		return duration;
	}

	/**
	 * update duration of the lyric
	 * <p>
	 * a dirty hack to avoid calculating duration every time
	 * </p>
	 * <strong>SHOULD BE CALLED AFTER CHANGING SENTENCES</strong>
	 */
	public void updateDuration() {
		this.duration = sentences == null || sentences.size() == 0 ? 0 :
				sentences.stream().sorted(Comparator.comparing(Sentence::getToTime))
				.mapToLong(Sentence::getToTime).max().orElseGet(sentences.get(sentences.size() - 1)::getToTime);
	}

	/**
	 * merge translation into lyric
	 * <p/>
	 * <strong>PLEASE NOTE THAT METHOD IS ONLY DESIGNED FOR NETEASE MUSIC's TRANSLATION</strong>,  in which every line of translation's time is the same as the original lyric
	 * @param translation the lyric's translation
	 */
	public void merge(Lyric translation){
		if (translation==null)
			return;
		if (translation.isEmpty())
			return;
		if (this.isEmpty()){
			this.sentences=translation.sentences;
			this.tags=translation.tags;
			this.updateDuration();
			return;
		}
		for (Sentence sentence : translation.sentences) {
			Sentence target = this.findSentence(sentence.getFromTime());
			if (target != null) {
				target.setContent(target.getContent() + "\n" + sentence.getContent());
			}
		}
		this.updateDuration();
	}
}
