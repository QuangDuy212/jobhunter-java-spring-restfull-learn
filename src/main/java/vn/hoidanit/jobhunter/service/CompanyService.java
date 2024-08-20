package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.repository.CompanyRespository;

@Service
public class CompanyService {
    private final CompanyRespository companyRespository;

    public CompanyService(CompanyRespository companyRespository) {
        this.companyRespository = companyRespository;
    }

    public Company handleCreateCompany(Company company) {
        return this.companyRespository.save(company);
    }

    public List<Company> fetchAllCompanies() {
        return this.companyRespository.findAll();
    }

    public Optional<Company> fetchCompanyById(long id) {
        Optional<Company> company = this.companyRespository.findById(id);
        return company;
    }

    public Company handleUpdateCompany(Company company) {
        Optional<Company> optionalCompany = this.fetchCompanyById(company.getId());
        if (optionalCompany.isPresent()) {
            Company currentCompany = optionalCompany.get();
            currentCompany.setName(company.getName());
            currentCompany.setAddress(company.getAddress());
            currentCompany.setDescription(company.getDescription());
            currentCompany.setLogo(company.getLogo());
            // update
            currentCompany = this.companyRespository.save(currentCompany);
            return currentCompany;
        }
        return null;
    }

    public void handleDeleteCompany(long id) {
        this.companyRespository.deleteById(id);
    }
}
