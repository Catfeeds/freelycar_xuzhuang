package com.geariot.platform.freelycar.dao.impl;

import com.geariot.platform.freelycar.dao.StaffDao;
import com.geariot.platform.freelycar.entities.ProjectInfo;
import com.geariot.platform.freelycar.entities.Staff;
import com.geariot.platform.freelycar.utils.Constants;
import com.geariot.platform.freelycar.utils.query.QueryUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class StaffDaoImpl implements StaffDao {

	@Autowired
	private SessionFactory sessionFactory;

	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	@Override
	public void saveStaff(Staff staff) {
		this.getSession().save(staff);

	}

	@Override
	public void deleteStaff(Staff staff) {
		this.getSession().delete(staff);
	}

	@Override
	public void deleteStaff(int staffId) {
		String hql = "delete from Staff where id = :staffId";
		this.getSession().createQuery(hql).setInteger("staffId", staffId).executeUpdate();
	}

	@Override
	public void deleteStaff(String staffName) {
		String hql = "delete form Staff where name = :staffName";
		this.getSession().createQuery(hql).setString("staffName", staffName).executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Staff> listStaffs(int from, int pageSize) {
		String hql = "from Staff";
		return this.getSession().createQuery(hql).setFirstResult(from).setMaxResults(pageSize)
				.setCacheable(Constants.SELECT_CACHE).list();
	}

	/*
	 * @SuppressWarnings("unchecked")
	 * 
	 * @Override public List<Staff> queryByNameAndId(int staffId, String
	 * staffName) { String hql =
	 * "from Staff where id like :staffId or name like :staffName"; return
	 * this.getSession().createQuery(hql).setInteger("staffId",
	 * staffId).setString("staffName", "%"+staffName+"%") .list(); }
	 */

	@Override
	public long getCount() {
		String hql = "select count(*) from Staff";
		return (long) this.getSession().createQuery(hql).setCacheable(Constants.SELECT_CACHE).uniqueResult();
	}

	/**
	 * 验证账号重复性（排除自己）
	 * @param staffId
	 * @param staffAccount
	 * @return
	 */
	@Override
	public Staff verifyTheRepeat(int staffId, String staffAccount) {
		String hql = "from Staff where account = :account and id <> :id";
		return (Staff) this.getSession().createQuery(hql).setString("account",staffAccount).setInteger("id",staffId).setCacheable(Constants.SELECT_CACHE).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Staff> getConditionQuery(String id , String name, int from, int pageSize) {
		QueryUtils qutils = new QueryUtils(getSession(), "from Staff");
		Query query = qutils.addString("id", id)
		.addStringLike("name", name)
		.setFirstResult(from)
		.setMaxResults(pageSize)
		.getQuery();
		return query.list();
	}

	@Override
	public Staff findStaffByStaffId(int staffId) {
		String hql = "from Staff where id = :staffId";
		return (Staff) getSession().createQuery(hql).setInteger("staffId", staffId).setCacheable(Constants.SELECT_CACHE)
				.uniqueResult();
	}

	@Override
	public Staff findStaffByPhone(String phone) {
		String hql = "from Staff where phone = :phone";
		return (Staff) getSession().createQuery(hql).setString("phone", phone).setCacheable(Constants.SELECT_CACHE)
				.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProjectInfo> staffServiceDetails(int staffId, int from, int pageSize) {
		String sql = "select projectInfoId from projectinfo_staff where staffId = :staffId";
		List<Object> projectInfoId = this.getSession().createSQLQuery(sql).setInteger("staffId", staffId).list();
		if (projectInfoId != null && !projectInfoId.isEmpty()) {
			String hql = "from ProjectInfo where id in :projectInfoId order by createDate desc";
			return this.getSession().createQuery(hql).setParameterList("projectInfoId", projectInfoId)
					.setFirstResult(from).setMaxResults(pageSize).setCacheable(Constants.SELECT_CACHE).list();
		} else {
			return null;
		}
	}

	@Override
	public long getConditionCount(String id , String name) {
		QueryUtils qutils = new QueryUtils(getSession(), "select count(*) from Staff");
		Query query = qutils.addString("id", id)
		.addStringLike("name", name)
		.getQuery();
		return (long) query.uniqueResult();
	}

}
